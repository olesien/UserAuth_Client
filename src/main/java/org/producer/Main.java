package org.producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class Main {
    static String hashPassword(String password) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);

        byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    static void run(Connection con, BufferedReader reader) throws IOException, NoSuchAlgorithmException, SQLException {
        System.out.println("Nyhetssida -> Kontohantering\n1. Skapa Konto\n2. Visa Konton \n3. Radera Konto\n4. Avsluta\n Välj ett alternativ: ");
        String character = reader.readLine();
        UserRepo userRepo = new UserRepo(con);

        switch (character) {
            case "1": {
                //Add
                System.out.println("Ange Namn: ");
                String name = reader.readLine();
                System.out.println("Ange E-post: ");
                String email = reader.readLine();
                System.out.println("Ange Ålder: ");
                int age = Integer.parseInt(reader.readLine());
                System.out.println("Ange Kön (M/F/Other: ");
                String gender = reader.readLine();
                if (!Objects.equals(gender, "M") && !Objects.equals(gender, "F") && !Objects.equals(gender, "Other")) {
                    //Error
                    System.out.println("Du måste skriva exakt M/F/Other");
                    break;
                }
                System.out.println("Ange Lösenord: ");
                String password = reader.readLine();

                String hashedPassword = hashPassword(password);
                userRepo.createUser(name, email, age, gender, hashedPassword);
                System.out.println("Konto sparat!");
                break;
            }
            case "2": {
                //List
                ArrayList<User> users = userRepo.getUsers();
                users.forEach(user -> {
                    System.out.printf("ID: %d, Namn: %s, E-post: %s, Ålder: %d, Kön: %s%n", user.getId(), user.getName(), user.getEmail(), user.getAge(), user.getGender());
                });
                break;
            }
            case "3": {
                System.out.println("Ange användar-ID att radera: ");
                int id = Integer.parseInt(reader.readLine());
                //Delete
                userRepo.deleteUser(id);

                System.out.println("Konto borttaget!");
                break;
            }
            case "4": {
                return;
            }
            default: {
                System.out.println("Du måste välja mellan 1 och 4");
                break;
            }
        }
        run(con, reader);
    }
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("Hello world!");
        try (var con =  DB.connect()){
            run(con, reader);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        reader.close();
    }
}