package app.security.daos;


import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.ApiException;
import app.security.exceptions.ValidationException;
import app.dtos.UserDTO;
import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityDAO implements ISecurityDAO {

    private static ISecurityDAO instance;
    private static EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public UserDTO getVerifiedUser(UserDTO userDTO) throws ValidationException {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :name", User.class);
            query.setParameter("name",userDTO.getUsername());
            List<User> users = query.getResultList();
            if (users.isEmpty())
                throw new EntityNotFoundException("No user found with username: " + userDTO.getUsername()); //RuntimeException
            users.get(0).getRoles().size(); // force roles to be fetched from db
            if (!users.get(0).verifyPassword(userDTO.getPassword()))
                throw new ValidationException("Wrong password");
            return new UserDTO(users.get(0).getId(), users.get(0).getUsername(), users.get(0).getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()));
        }
    }

    @Override
    public User createUser(UserDTO userDTO) {
        try (EntityManager em = getEntityManager()) {
            User userEntity = em.find(User.class, userDTO.getId());
            if (userEntity != null)
                throw new EntityExistsException("User with username: " + userDTO.getUsername() + " already exists");
            userEntity = new User(userDTO.getUsername(), userDTO.getPassword());
            em.getTransaction().begin();
            Role userRole = em.find(Role.class, "user");
            if (userRole == null)
                userRole = new Role("user");
            em.persist(userRole);
            userEntity.addRole(userRole);
            em.persist(userEntity);
            em.getTransaction().commit();
            return userEntity;
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException(400, e.getMessage());
        }
    }

    @Override
    public User addRole(UserDTO userDTO, String newRole) {
        try (EntityManager em = getEntityManager()) {
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :name", User.class)
                    .setParameter("name", userDTO.getUsername())
                    .getSingleResult();
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + userDTO.getUsername());
            em.getTransaction().begin();
                Role role = em.find(Role.class, newRole);
                if (role == null) {
                    role = new Role(newRole);
                    em.persist(role);
                }
                user.addRole(role);
                //em.merge(user);
            em.getTransaction().commit();
            return user;
        }
    }
}

