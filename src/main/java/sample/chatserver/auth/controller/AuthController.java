
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    public AuthController(AuthenticationManager authenticationManager,
                         JwtService jwtService,
                         UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            // 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
            
            String username = authentication.getName();
            
            // 토큰 생성
            String accessToken = jwtService.generateAccessToken(username);
            String refreshToken = jwtService.generateRefreshToken(username);
            
            AuthResponse response = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(900) // 15분
                    .username(username)
                    .build();
            
            log.info("User {} logged in successfully", username);
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user: {}", authRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed"));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            
            if (!jwtService.validateRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired refresh token"));
            }
            
            String username = jwtService.getUsernameFromToken(refreshToken);
            
            // 사용자 존재 확인
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 새로운 Access Token 생성
            String newAccessToken = jwtService.generateAccessToken(username);
            
            AuthResponse response = AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // 기존 refresh token 재사용
                    .tokenType("Bearer")
                    .expiresIn(900) // 15분
                    .username(username)
                    .build();
            
            log.info("Token refreshed for user: {}", username);
            return ResponseEntity.ok(response);
            
        } catch (UsernameNotFoundException e) {
            log.warn("User not found during token refresh: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        } catch (Exception e) {
            log.error("Token refresh error: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token refresh failed"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token != null) {
                String username = jwtService.getUsernameFromToken(token);
                jwtService.deleteRefreshToken(username);
                log.info("User {} logged out successfully", username);
            }
            
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
            
        } catch (Exception e) {
            log.error("Logout error: ", e);
            return ResponseEntity.ok(Map.of("message", "Logout completed"));
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}