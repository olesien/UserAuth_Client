package org.producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

    static void run(BufferedReader reader) throws IOException, NoSuchAlgorithmException {
        System.out.println("Leveranstjänst -> Kontohantering\n1. Skapa Konto\n2. Visa Konton \n3. Radera Konto\n4. Uppdatera samtycke\n5. Avsluta\n Välj ett alternativ: ");
        String character = reader.readLine();
        UserAPI userRepo = new UserAPI();

        switch (character) {
            case "1": {
                //Add
                System.out.println("Ange Namn: ");
                String name = reader.readLine();
                System.out.println("Ange E-post: ");
                String email = reader.readLine();
                System.out.println("Ange Ålder (kan lämna blankt): ");
                String ageText = reader.readLine();
                Integer age;
                if (!Objects.equals(ageText, "")) {
                    age = Integer.parseInt(ageText);
                } else {
                    age = null;
                }
                System.out.println("Ange Kön (M/F/Other)  (kan lämna blankt): ");
                String gender = reader.readLine();
                if (Objects.equals(gender, "")) {
                    gender = null;
                } else
                if (!Objects.equals(gender, "M") && !Objects.equals(gender, "F") && !Objects.equals(gender, "Other")) {
                    //Error
                    System.out.println("Du måste skriva exakt M/F/Other");
                    break;
                }
                System.out.println("Ange Lösenord: ");
                String password = reader.readLine();

                String hashedPassword = hashPassword(password);
                boolean success = userRepo.createUser(name, email, age, gender, hashedPassword);
                if (success) {
                    System.out.println("Konto sparat!");

                } else {
                    System.out.println("Konto har inte sparats! : (");

                }
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
                boolean success = userRepo.deleteUser(id);
                if (success) {
                    System.out.println("Konto borttaget!");
                } else {
                    System.out.println("Konto kunde tyvärr inte tas bort");
                }

                break;
            }
            case "4": {
                System.out.println("Ange användar-ID att uppdatera: ");
                int id = Integer.parseInt(reader.readLine());

                System.out.println("Samtycke för cookies (true/false): ");
                boolean cookies = Boolean.parseBoolean(reader.readLine());

                System.out.println("Samtycke för dataanvändning (true/false): ");
                boolean data = Boolean.parseBoolean(reader.readLine());

                boolean success = userRepo.updateConsent(id, cookies, data);
                if (success) {
                    System.out.println("Samtycke uppdaterat!");
                } else {
                    System.out.println("Samtycke kunde tyvärr ej uppdateras. Har du rätt ID?");
                }
                break;
            }
            case "5": {
                return;
            }
            default: {
                System.out.println("Du måste välja mellan 1 och 4");
                break;
            }
        }
        run(reader);
    }
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("REQ");
//        //Req test
//        int i = 0;
//        while (i < 1000) {
//            i++;
//            System.out.println(i);
//            Thread thread = new Thread(() -> {
//                URL url = null;
//                try {
//                    url = new URL("http://localhost:8080/test");
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.connect();
//                    System.out.println(conn.getResponseCode());
//                } catch (MalformedURLException e) {
//                    System.out.println(e);
//                } catch (IOException e) {
//                    System.out.println(e);
//                }
//
//            });
//            thread.start();
//        }
        try {
            run(reader);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        reader.close();
    }
}