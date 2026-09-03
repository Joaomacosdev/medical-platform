        package br.com.medical.authservice.domain.user.entities;

import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.domain.user.exception.InvalidEmailException;
import br.com.medical.authservice.domain.user.exception.InvalidPasswordException;
import br.com.medical.authservice.domain.user.exception.InvalidRoleException;
import br.com.medical.authservice.domain.user.exception.InvalidUserNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Domain Entity Tests")
class UserTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Doe";
    private static final String EMAIL = "john.doe@email.com";
    private static final String PASSWORD = "password123";
    private static final Role ROLE = Role.MEDICO;

    private User createUser() {
        return User.builder()
                .id(USER_ID)
                .userName(USER_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .role(ROLE)
                .build();
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Should create a valid user")
        void shouldCreateValidUser() {

            User user = User.builder()
                    .id(USER_ID)
                    .userName(USER_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .role(ROLE)
                    .build();

            assertAll(
                    () -> assertEquals(USER_ID, user.getId()),
                    () -> assertEquals(USER_NAME, user.getUserName()),
                    () -> assertEquals(EMAIL, user.getEmail()),
                    () -> assertEquals(PASSWORD, user.getPassword()),
                    () -> assertEquals(ROLE, user.getRole()),
                    () -> assertTrue(user.isActive())
            );
        }

        @Test
        @DisplayName("Should create user as active by default")
        void shouldCreateUserAsActiveByDefault() {

            User user = User.builder()
                    .userName(USER_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .role(ROLE)
                    .build();

            assertTrue(user.isActive());
        }

        @Test
        @DisplayName("Should allow creating an inactive user")
        void shouldAllowCreatingInactiveUser() {

            User user = User.builder()
                    .userName(USER_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .role(ROLE)
                    .isActive(false)
                    .build();

            assertFalse(user.isActive());
        }
    }

    @Nested
    @DisplayName("Getters")
    class GetterTests {

        @Test
        @DisplayName("Should return user id")
        void shouldReturnUserId() {

            User user = createUser();

            assertEquals(USER_ID, user.getId());
        }

        @Test
        @DisplayName("Should return username")
        void shouldReturnUsername() {

            User user = createUser();

            assertEquals(USER_NAME, user.getUserName());
        }

        @Test
        @DisplayName("Should return email")
        void shouldReturnEmail() {

            User user = createUser();

            assertEquals(EMAIL, user.getEmail());
        }

        @Test
        @DisplayName("Should return password")
        void shouldReturnPassword() {

            User user = createUser();

            assertEquals(PASSWORD, user.getPassword());
        }

        @Test
        @DisplayName("Should return role")
        void shouldReturnRole() {

            User user = createUser();

            assertEquals(ROLE, user.getRole());
        }

        @Test
        @DisplayName("Should return active status")
        void shouldReturnActiveStatus() {

            User user = createUser();

            assertTrue(user.isActive());
        }
    }

    @Nested
    @DisplayName("Activation")
    class ActivationTests {

        @Test
        @DisplayName("Should activate inactive user")
        void shouldActivateInactiveUser() {

            User user = User.builder()
                    .userName(USER_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .role(ROLE)
                    .isActive(false)
                    .build();

            user.activate();

            assertTrue(user.isActive());
        }

        @Test
        @DisplayName("Should remain active when already active")
        void shouldRemainActiveWhenAlreadyActive() {

            User user = createUser();

            user.activate();

            assertTrue(user.isActive());
        }
    }

    @Nested
    @DisplayName("Deactivation")
    class DeactivationTests {

        @Test
        @DisplayName("Should deactivate active user")
        void shouldDeactivateActiveUser() {

            User user = createUser();

            user.deactivate();

            assertFalse(user.isActive());
        }

        @Test
        @DisplayName("Should remain inactive when already inactive")
        void shouldRemainInactiveWhenAlreadyInactive() {

            User user = User.builder()
                    .userName(USER_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .role(ROLE)
                    .isActive(false)
                    .build();

            user.deactivate();

            assertFalse(user.isActive());
        }
    }

    @Nested
    @DisplayName("Update Profile")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should update username and email")
        void shouldUpdateUsernameAndEmail() {

            User user = createUser();

            user.updateProfile(
                    "Jane Doe",
                    "jane.doe@email.com"
            );

            assertAll(
                    () -> assertEquals("Jane Doe", user.getUserName()),
                    () -> assertEquals("jane.doe@email.com", user.getEmail())
            );
        }

        @Test
        @DisplayName("Should normalize email when updating profile")
        void shouldNormalizeEmailWhenUpdatingProfile() {

            User user = createUser();

            user.updateProfile(
                    "Jane Doe",
                    "  JANE.DOE@EMAIL.COM  "
            );

            assertEquals(
                    "jane.doe@email.com",
                    user.getEmail()
            );
        }

        @Test
        @DisplayName("Should not change profile when username is invalid")
        void shouldNotChangeProfileWhenUsernameIsInvalid() {

            User user = createUser();

            String originalUsername = user.getUserName();
            String originalEmail = user.getEmail();

            assertThrows(
                    InvalidUserNameException.class,
                    () -> user.updateProfile(
                            "   ",
                            "new@email.com"
                    )
            );

            assertAll(
                    () -> assertEquals(originalUsername, user.getUserName()),
                    () -> assertEquals(originalEmail, user.getEmail())
            );
        }

        @Test
        @DisplayName("Should not change profile when email is invalid")
        void shouldNotChangeProfileWhenEmailIsInvalid() {

            User user = createUser();

            String originalUsername = user.getUserName();
            String originalEmail = user.getEmail();

            assertThrows(
                    InvalidEmailException.class,
                    () -> user.updateProfile(
                            "Jane Doe",
                            "invalid-email"
                    )
            );

            assertAll(
                    () -> assertEquals(originalUsername, user.getUserName()),
                    () -> assertEquals(originalEmail, user.getEmail())
            );
        }
    }

    @Nested
    @DisplayName("Change Password")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password when password is valid")
        void shouldChangePasswordWhenPasswordIsValid() {

            User user = createUser();

            user.changePassword("newPassword123");

            assertEquals(
                    "newPassword123",
                    user.getPassword()
            );
        }

        @Test
        @DisplayName("Should reject password shorter than eight characters")
        void shouldRejectPasswordShorterThanEightCharacters() {

            User user = createUser();

            assertThrows(
                    InvalidPasswordException.class,
                    () -> user.changePassword("abc123")
            );

            assertEquals(
                    PASSWORD,
                    user.getPassword()
            );
        }

        @Test
        @DisplayName("Should reject password without letters")
        void shouldRejectPasswordWithoutLetters() {

            User user = createUser();

            assertThrows(
                    InvalidPasswordException.class,
                    () -> user.changePassword("12345678")
            );

            assertEquals(
                    PASSWORD,
                    user.getPassword()
            );
        }

        @Test
        @DisplayName("Should reject password without numbers")
        void shouldRejectPasswordWithoutNumbers() {

            User user = createUser();

            assertThrows(
                    InvalidPasswordException.class,
                    () -> user.changePassword("abcdefgh")
            );

            assertEquals(
                    PASSWORD,
                    user.getPassword()
            );
        }

        @Test
        @DisplayName("Should reject null password")
        void shouldRejectNullPassword() {

            User user = createUser();

            assertThrows(
                    InvalidPasswordException.class,
                    () -> user.changePassword(null)
            );

            assertEquals(
                    PASSWORD,
                    user.getPassword()
            );
        }

        @Test
        @DisplayName("Should reject blank password")
        void shouldRejectBlankPassword() {

            User user = createUser();

            assertThrows(
                    InvalidPasswordException.class,
                    () -> user.changePassword("        ")
            );

            assertEquals(
                    PASSWORD,
                    user.getPassword()
            );
        }
    }

    @Nested
    @DisplayName("Change Role")
    class ChangeRoleTests {

        @Test
        @DisplayName("Should change role")
        void shouldChangeRole() {

            User user = createUser();

            user.changeRole(Role.ADMIN);

            assertEquals(Role.ADMIN, user.getRole());
        }

        @Test
        @DisplayName("Should reject null role")
        void shouldRejectNullRole() {

            User user = createUser();

            assertThrows(
                    InvalidRoleException.class,
                    () -> user.changeRole(null)
            );

            assertEquals(ROLE, user.getRole());
        }
    }

    @Nested
    @DisplayName("Username Validation")
    class UsernameValidationTests {

        @Test
        @DisplayName("Should reject null username")
        void shouldRejectNullUsername() {

            assertThrows(
                    InvalidUserNameException.class,
                    () -> User.builder()
                            .userName(null)
                            .email(EMAIL)
                            .password(PASSWORD)
                            .role(ROLE)
                            .build()
            );
        }

        @Test
        @DisplayName("Should reject blank username")
        void shouldRejectBlankUsername() {

            assertThrows(
                    InvalidUserNameException.class,
                    () -> User.builder()
                            .userName("   ")
                            .email(EMAIL)
                            .password(PASSWORD)
                            .role(ROLE)
                            .build()
            );
        }

        @Test
        @DisplayName("Should reject empty username")
        void shouldRejectEmptyUsername() {

            assertThrows(
                    InvalidUserNameException.class,
                    () -> User.builder()
                            .userName("")
                            .email(EMAIL)
                            .password(PASSWORD)
                            .role(ROLE)
                            .build()
            );
        }
    }

    @Nested
    @DisplayName("Email Validation")
    class EmailValidationTests {

        @Test
        @DisplayName("Should normalize email to lowercase")
        void shouldNormalizeEmailToLowercase() {

            User user = User.builder()
                    .userName(USER_NAME)
                    .email("JOHN.DOE@EMAIL.COM")
                    .password(PASSWORD)
                    .role(ROLE)
                    .build();

            assertEquals(
                    "john.doe@email.com",
                    user.getEmail()
            );
        }

        @Test
        @DisplayName("Should remove surrounding spaces from email")
        void shouldRemoveSurroundingSpacesFromEmail() {

            User user = User.builder()
                    .userName(USER_NAME)
                    .email("  john.doe@email.com  ")
                    .password(PASSWORD)
                    .role(ROLE)
                    .build();

            assertEquals(
                    "john.doe@email.com",
                    user.getEmail()
            );
        }

        @Test
        @DisplayName("Should reject null email")
        void shouldRejectNullEmail() {

            assertThrows(
                    InvalidEmailException.class,
                    () -> User.builder()
                            .userName(USER_NAME)
                            .email(null)
                            .password(PASSWORD)
                            .role(ROLE)
                            .build()
            );
        }

        @Test
        @DisplayName("Should reject blank email")
        void shouldRejectBlankEmail() {

            assertThrows(
                    InvalidEmailException.class,
                    () -> User.builder()
                            .userName(USER_NAME)
                            .email("   ")
                            .password(PASSWORD)
                            .role(ROLE)
                            .build()
            );
        }

        @Test
        @DisplayName("Should reject malformed email")
        void shouldRejectMalformedEmail() {

            assertThrows(
                    InvalidEmailException.class,
                    () -> User.builder()
                            .userName(USER_NAME)
                            .email("invalid-email")
                            .password(PASSWORD)
                            .role(ROLE)
                            .build()
            );
        }

        @Test
        @DisplayName("Should reject email without domain")
        void shouldRejectEmailWithoutDomain() {

            assertThrows(
                    InvalidEmailException.class,
                    () -> User.builder()
                            .userName(USER_NAME)
                            .email("john@")
                            .password(PASSWORD)
                            .role(ROLE)
                            .build()
            );
        }
    }

    @Nested
    @DisplayName("Password Validation")
    class PasswordValidationTests {

        @Test
        @DisplayName("Should accept valid password")
        void shouldAcceptValidPassword() {

            assertDoesNotThrow(
                    () -> User.builder()
                            .userName(USER_NAME)
                            .email(EMAIL)
                            .password("password123")
                            .role(ROLE)
                            .build()
            );
        }

        @Test
        @DisplayName("Should reject null password")
        void shouldRejectNullPassword() {

            assertThrows(
                    InvalidPasswordException.class,
                    () -> User.builder()
                            .userName(USER_NAME)
                            .email(EMAIL)
                            .password(null)
                            .role(ROLE)
                            .build()
            );
        }

        @Test
        @DisplayName("Should reject blank password")
        void shouldRejectBlankPassword() {

            assertThrows(
                    InvalidPasswordException.class,
                    () -> User.builder()
                            .userName(USER_NAME)
                            .email(EMAIL)
                            .password("        ")
                            .role(ROLE)
                            .build()
            );
        }

        @Test
        @DisplayName("Should reject password without letters")
        void shouldRejectPasswordWithoutLetters() {

            assertThrows(
                    InvalidPasswordException.class,
                    () -> User.builder()
                            .userName(USER_NAME)
                            .email(EMAIL)
                            .password("12345678")
                            .role(ROLE)
                            .build()
            );
        }

        @Test
        @DisplayName("Should reject password without numbers")
        void shouldRejectPasswordWithoutNumbers() {

            assertThrows(
                    InvalidPasswordException.class,
                    () -> User.builder()
                            .userName(USER_NAME)
                            .email(EMAIL)
                            .password("abcdefgh")
                            .role(ROLE)
                            .build()
            );
        }
    }

    @Nested
    @DisplayName("Role Validation")
    class RoleValidationTests {

        @Test
        @DisplayName("Should reject null role")
        void shouldRejectNullRole() {

            assertThrows(
                    InvalidRoleException.class,
                    () -> User.builder()
                            .userName(USER_NAME)
                            .email(EMAIL)
                            .password(PASSWORD)
                            .role(null)
                            .build()
            );
        }
    }

    @Nested
    @DisplayName("Equals and HashCode")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when users have the same id")
        void shouldBeEqualWhenUsersHaveSameId() {

            User firstUser = User.builder()
                    .id(1L)
                    .userName("John")
                    .email("john@email.com")
                    .password("password123")
                    .role(Role.MEDICO)
                    .build();

            User secondUser = User.builder()
                    .id(1L)
                    .userName("Jane")
                    .email("jane@email.com")
                    .password("different123")
                    .role(Role.ADMIN)
                    .build();

            assertEquals(firstUser, secondUser);
        }

        @Test
        @DisplayName("Should have same hash code when users have same id")
        void shouldHaveSameHashCodeWhenUsersHaveSameId() {

            User firstUser = User.builder()
                    .id(1L)
                    .userName("John")
                    .email("john@email.com")
                    .password("password123")
                    .role(Role.MEDICO)
                    .build();

            User secondUser = User.builder()
                    .id(1L)
                    .userName("Jane")
                    .email("jane@email.com")
                    .password("different123")
                    .role(Role.ADMIN)
                    .build();

            assertEquals(
                    firstUser.hashCode(),
                    secondUser.hashCode()
            );
        }

        @Test
        @DisplayName("Should not be equal when users have different ids")
        void shouldNotBeEqualWhenUsersHaveDifferentIds() {

            User firstUser = User.builder()
                    .id(1L)
                    .userName(USER_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .role(ROLE)
                    .build();

            User secondUser = User.builder()
                    .id(2L)
                    .userName(USER_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .role(ROLE)
                    .build();

            assertNotEquals(firstUser, secondUser);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {

            User user = createUser();

            assertNotEquals(null, user);
        }

        @Test
        @DisplayName("Should not be equal to another type")
        void shouldNotBeEqualToAnotherType() {

            User user = createUser();

            assertNotEquals(user, "user");
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {

            User user = createUser();

            assertEquals(user, user);
        }

        @Test
        @DisplayName("Should have different hash codes for different ids")
        void shouldHaveDifferentHashCodesForDifferentIds() {

            User firstUser = User.builder()
                    .id(1L)
                    .userName(USER_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .role(ROLE)
                    .build();

            User secondUser = User.builder()
                    .id(2L)
                    .userName(USER_NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .role(ROLE)
                    .build();

            assertNotEquals(
                    firstUser.hashCode(),
                    secondUser.hashCode()
            );
        }
    }
}
