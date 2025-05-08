package Travel_Booking;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private UserRole role;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private boolean isActive;
    private String profilePicturePath;

    public enum UserRole {
        USER, ADMIN;

        public static UserRole fromString(String role) {
            try {
                return UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                return USER;
            }
        }
    }

    // Validation patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[0-9\\s-]{7,}$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    // Constructors
    public User() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.isActive = true;
    }

    public User(String username, String email, String password) {
        this();
        this.username = username;
        setEmail(email);
        setPassword(password);
    }
    
    

    // Enhanced getters and setters with validation
    public int getUserId() { return userId; }
    public void setUserId(int userId) { 
        if(userId < 0) throw new IllegalArgumentException("Invalid user ID");
        this.userId = userId; 
    }
    
    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }
    
    

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if(username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username.trim();
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPassword(String plainPassword) {
        if(plainPassword == null || plainPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " 
                + MIN_PASSWORD_LENGTH + " characters");
        }
        this.passwordHash = PasswordUtils.hashPassword(plainPassword);
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if(email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.toLowerCase();
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName.trim() : null;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        if(phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.phone = phone;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = address != null ? address.trim() : null;
    }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { 
        this.role = role != null ? role : UserRole.USER;
    }
    
    public void setRoleFromString(String role) {
        this.role = UserRole.fromString(role);
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt != null ? createdAt : new Timestamp(System.currentTimeMillis());
    }

    public Timestamp getLastLogin() { return lastLogin; }
    public void updateLastLogin() {
        this.lastLogin = new Timestamp(System.currentTimeMillis());
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Security methods
    public boolean verifyPassword(String plainPassword) {
        return PasswordUtils.checkPassword(plainPassword, this.passwordHash);
    }

    // Business logic methods
    public boolean hasAdminAccess() {
        return this.role == UserRole.ADMIN;
    }

    // Data serialization
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("username", username);
        map.put("email", email);
        map.put("fullName", fullName);
        map.put("phone", phone);
        map.put("address", address);
        map.put("role", role.name());
        map.put("createdAt", createdAt);
        map.put("lastLogin", lastLogin);
        map.put("isActive", isActive);
        map.put("profilePicturePath", profilePicturePath);
        return map;
    }
    
    public void updateProfile(String fullName, String email, String phone, 
                            String address, String profilePicturePath) {
        setFullName(fullName);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
        setProfilePicturePath(profilePicturePath);
        updateLastLogin();
    }

    @Override
    public String toString() {
        return "User{" +
            "userId=" + userId +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", role=" + role +
            ", active=" + isActive +
            '}';
    }
}

  