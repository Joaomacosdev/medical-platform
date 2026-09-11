package br.com.medical.authservice.infra.user.adapter;

import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.infra.user.mapper.UserPersistenceMapper;
import br.com.medical.authservice.infra.user.persistence.entity.UserJpaEntity;
import br.com.medical.authservice.infra.user.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserJpaAdapterTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Doe";
    private static final String EMAIL = "john@example.com";
    private static final String PASSWORD = "Password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedPassword";

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserPersistenceMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserJpaAdapter(
                userJpaRepository,
                mapper,
                passwordEncoder
        );
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        User user = createUser();
        UserJpaEntity entity = createEntity();
        UserJpaEntity savedEntity = createEntity();

        User mappedUser = createUser();

        when(mapper.toJpa(user))
                .thenReturn(entity);

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        when(userJpaRepository.save(entity))
                .thenReturn(savedEntity);

        when(mapper.toDomain(savedEntity))
                .thenReturn(mappedUser);

        User result = adapter.save(user);

        assertNotNull(result);
        assertSame(mappedUser, result);

        verify(mapper).toJpa(user);
        verify(passwordEncoder).encode(PASSWORD);
        verify(userJpaRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("Should encode password before saving user")
    void shouldEncodePasswordBeforeSaving() {
        User user = createUser();
        UserJpaEntity entity = createEntity();
        UserJpaEntity savedEntity = createEntity();

        when(mapper.toJpa(user))
                .thenReturn(entity);

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        when(userJpaRepository.save(entity))
                .thenReturn(savedEntity);

        when(mapper.toDomain(savedEntity))
                .thenReturn(user);

        adapter.save(user);

        InOrder inOrder = inOrder(
                mapper,
                passwordEncoder,
                userJpaRepository
        );

        inOrder.verify(mapper).toJpa(user);
        inOrder.verify(passwordEncoder).encode(PASSWORD);
        inOrder.verify(userJpaRepository).save(entity);
    }

    @Test
    @DisplayName("Should save encoded password in JPA entity")
    void shouldSaveEncodedPasswordInEntity() {
        User user = createUser();
        UserJpaEntity entity = createEntity();
        UserJpaEntity savedEntity = createEntity();

        when(mapper.toJpa(user))
                .thenReturn(entity);

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        when(userJpaRepository.save(entity))
                .thenReturn(savedEntity);

        when(mapper.toDomain(savedEntity))
                .thenReturn(user);

        adapter.save(user);

        assertEquals(ENCODED_PASSWORD, entity.getPassword());

        verify(userJpaRepository).save(entity);
    }

    @Test
    @DisplayName("Should find user by id")
    void shouldFindUserById() {
        UserJpaEntity entity = createEntity();
        User user = createUser();

        when(userJpaRepository.findById(USER_ID))
                .thenReturn(Optional.of(entity));

        when(mapper.toDomain(entity))
                .thenReturn(user);

        Optional<User> result = adapter.findById(USER_ID);

        assertTrue(result.isPresent());
        assertSame(user, result.get());

        verify(userJpaRepository).findById(USER_ID);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should return empty optional when user is not found by id")
    void shouldReturnEmptyWhenUserIsNotFoundById() {
        when(userJpaRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        Optional<User> result = adapter.findById(USER_ID);

        assertTrue(result.isEmpty());

        verify(userJpaRepository).findById(USER_ID);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        UserJpaEntity entity = createEntity();
        User user = createUser();

        when(userJpaRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(entity));

        when(mapper.toDomain(entity))
                .thenReturn(user);

        Optional<User> result = adapter.findByEmail(EMAIL);

        assertTrue(result.isPresent());
        assertSame(user, result.get());

        verify(userJpaRepository).findByEmail(EMAIL);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should return empty optional when user is not found by email")
    void shouldReturnEmptyWhenUserIsNotFoundByEmail() {
        when(userJpaRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail(EMAIL);

        assertTrue(result.isEmpty());

        verify(userJpaRepository).findByEmail(EMAIL);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUserName() {
        UserJpaEntity entity = createEntity();
        User user = createUser();

        when(userJpaRepository.findByUserName(USER_NAME))
                .thenReturn(Optional.of(entity));

        when(mapper.toDomain(entity))
                .thenReturn(user);

        Optional<User> result = adapter.findByUserName(USER_NAME);

        assertTrue(result.isPresent());
        assertSame(user, result.get());

        verify(userJpaRepository).findByUserName(USER_NAME);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should return empty optional when user is not found by username")
    void shouldReturnEmptyWhenUserIsNotFoundByUserName() {
        when(userJpaRepository.findByUserName(USER_NAME))
                .thenReturn(Optional.empty());

        Optional<User> result = adapter.findByUserName(USER_NAME);

        assertTrue(result.isEmpty());

        verify(userJpaRepository).findByUserName(USER_NAME);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        UserJpaEntity entity1 = createEntity(1L, "John", "john@example.com");
        UserJpaEntity entity2 = createEntity(2L, "Jane", "jane@example.com");

        User user1 = createUser(1L, "John", "john@example.com");
        User user2 = createUser(2L, "Jane", "jane@example.com");

        when(userJpaRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        when(mapper.toDomain(entity1))
                .thenReturn(user1);

        when(mapper.toDomain(entity2))
                .thenReturn(user2);

        List<User> result = adapter.findAll();

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertSame(user1, result.get(0)),
                () -> assertSame(user2, result.get(1))
        );

        verify(userJpaRepository).findAll();
        verify(mapper).toDomain(entity1);
        verify(mapper).toDomain(entity2);
    }

    @Test
    @DisplayName("Should return empty list when there are no users")
    void shouldReturnEmptyListWhenThereAreNoUsers() {
        when(userJpaRepository.findAll())
                .thenReturn(List.of());

        List<User> result = adapter.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userJpaRepository).findAll();
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("Should delete user by id")
    void shouldDeleteUserById() {
        adapter.deleteById(USER_ID);

        verify(userJpaRepository).deleteById(USER_ID);
    }

    @Test
    @DisplayName("Should check if user exists by id")
    void shouldCheckIfUserExistsById() {
        when(userJpaRepository.existsById(USER_ID))
                .thenReturn(true);

        boolean result = adapter.existsById(USER_ID);

        assertTrue(result);

        verify(userJpaRepository).existsById(USER_ID);
    }

    @Test
    @DisplayName("Should return false when user does not exist by id")
    void shouldReturnFalseWhenUserDoesNotExistById() {
        when(userJpaRepository.existsById(USER_ID))
                .thenReturn(false);

        boolean result = adapter.existsById(USER_ID);

        assertFalse(result);

        verify(userJpaRepository).existsById(USER_ID);
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        when(userJpaRepository.existsByEmail(EMAIL))
                .thenReturn(true);

        boolean result = adapter.existsByEmail(EMAIL);

        assertTrue(result);

        verify(userJpaRepository).existsByEmail(EMAIL);
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void shouldReturnFalseWhenUserDoesNotExistByEmail() {
        when(userJpaRepository.existsByEmail(EMAIL))
                .thenReturn(false);

        boolean result = adapter.existsByEmail(EMAIL);

        assertFalse(result);

        verify(userJpaRepository).existsByEmail(EMAIL);
    }

    private User createUser() {
        return createUser(
                USER_ID,
                USER_NAME,
                EMAIL
        );
    }

    private User createUser(
            Long id,
            String userName,
            String email
    ) {
        return User.builder()
                .id(id)
                .userName(userName)
                .email(email)
                .password(PASSWORD)
                .role(Role.MEDICO)
                .isActive(true)
                .build();
    }

    private UserJpaEntity createEntity() {
        return createEntity(
                USER_ID,
                USER_NAME,
                EMAIL
        );
    }

    private UserJpaEntity createEntity(
            Long id,
            String userName,
            String email
    ) {

        UserJpaEntity entity = new UserJpaEntity();

        entity.setId(id);
        entity.setUserName(userName);
        entity.setEmail(email);
        entity.setPassword(PASSWORD);
        entity.setRole(Role.MEDICO);
        entity.setActive(true);

        return entity;
    }
}