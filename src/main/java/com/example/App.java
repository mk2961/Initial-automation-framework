package com.example;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello, practice-java!");
        int a = 2, b = 3;
        System.out.printf("%d + %d = %d%n", a, b, add(a, b));
    }

    public static int add(int x, int y) {
        return x + y;
    }
}
