package com.example.graphQL.resolver;

import com.example.graphQL.entity.User;
import com.example.graphQL.exception.UserNotFoundException;
import com.example.graphQL.model.UserInput;
import com.example.graphQL.repository.UserRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class UserResolver {

    private final UserRepository userRepository;

    public UserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @QueryMapping
    public List<User> users() {
        return userRepository.findAll();
    }

    @QueryMapping
    public User user(@Argument Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @MutationMapping
    public User createUser(@Argument UserInput input) {
        validate(input);
        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setAge(input.getAge());
        return userRepository.save(user);
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument UserInput input) {
        validate(input);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id + " to update the record"));
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setAge(input.getAge());
        return userRepository.save(user);
    }

    @MutationMapping
    public boolean deleteUser(@Argument Long id) {
        userRepository.deleteById(id);
        return true;
    }

    private void validate(UserInput input) {
        if (input.getAge() < 18)
            throw new IllegalArgumentException("User must be 18+");
        if (!input.getEmail().contains("@"))
            throw new IllegalArgumentException("Invalid email address");
    }

}
