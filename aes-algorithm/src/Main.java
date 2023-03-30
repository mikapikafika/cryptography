import java.util.*;

public class Main {
    public static void showMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("What you want to do?");
            System.out.println("[1] - Encode a file");
            System.out.println("[2] - Decode a file");
            System.out.println("[3] - Exit the program");
            System.out.print("Enter a number: ");

            int num = scanner.nextInt();
            System.out.println("\n\n\n");

            switch (num) {
                case 1 -> {
                    // blyblybly
                }
                case 2 -> {
                    // blyblyblyyyy
                }
                case 3 -> {
                    scanner.close();
                    return;
                }
                default -> {
                    System.out.println("Invalid option selected, leaving the program... ");
                    scanner.close();
                    return;
                }
            }
        }
    }


    public static void main(String[] args) {
        showMenu();
    }
}
