package org.adithyagallage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class App {
    public static void main(String[] args) {
        // configuration
        boolean USE_DB_CONNECTION_POOL = true;
        boolean USE_THREAD_POOL = true;

        DatabaseSeeder seeder = new DatabaseSeeder(USE_DB_CONNECTION_POOL);
        seeder.clean();
        seeder.seed();
        List<UserCredential> userCredentialList = new ArrayList<>();
        for (int i = 1; i <= 1_000_000; i++) {
            UserCredential userCredential = new UserCredential();
            userCredential.setEmail("user" + i + "@example.com");
            userCredential.setPassword("pass" + i);
            userCredentialList.add(userCredential);
        }

        /*
        * int availableCores = Runtime.getRuntime().availableProcessors(); // e.g., 8 cores
        * int poolSize = availableCores * 3; // safe starting point for IO-bound
        * */
        ForkJoinPool customThreadPool = new ForkJoinPool(25);

        // starting the validation process
        long startTime = System.currentTimeMillis();
        List<User> usersList;
        if(USE_THREAD_POOL) {
            List<CompletableFuture<User>> futures  = userCredentialList.stream().map(
                    userCredential -> CompletableFuture.supplyAsync(() -> {
                        UserCredentialValidationJob userCredentialValidationJob = new UserCredentialValidationJob(userCredential.getEmail(), userCredential.getPassword(), USE_DB_CONNECTION_POOL);
                        return userCredentialValidationJob.validate();
                    }, customThreadPool)
            ).toList();

            // Wait for all futures to complete
            usersList = futures.stream().map(CompletableFuture::join).toList();
        } else {
            usersList  = userCredentialList.stream().map(
                    userCredential -> {
                        UserCredentialValidationJob userCredentialValidationJob = new UserCredentialValidationJob(userCredential.getEmail(), userCredential.getPassword(), USE_DB_CONNECTION_POOL);
                        return userCredentialValidationJob.validate();
                    }
            ).toList();
        }

        // End timer
        long endTime = System.currentTimeMillis();
        long elapsedMillis = endTime - startTime;
        double elapsedSeconds = elapsedMillis / 1000.0;
        System.out.println("Elapsed time: " + elapsedSeconds + " s");

        int guestUserCount = usersList.stream().filter(user -> user.isGuestUser()).toList().size();
        System.out.println(guestUserCount);
    }
}
