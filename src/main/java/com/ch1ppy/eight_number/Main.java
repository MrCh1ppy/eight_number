package com.ch1ppy.eight_number;

import com.ch1ppy.eight_number.dealer.EightNumberHelper;

import java.util.Scanner;

/**
 * @author 橙鼠鼠
 */
public class Main {
    public static int[][] inputNumber(){
        Scanner scanner = new Scanner(System.in);
        final int[][] res = new int[2][9];
        System.out.println("请输入初始状态：");
        for (int i = 0; i < res[0].length; i++) {
            res[0][i]=scanner.nextInt();
        }
        System.out.println("请输入目标状态：");
        for (int i = 0; i < res[0].length; i++) {
            res[1][i]=scanner.nextInt();
        }
        scanner.close();
        System.out.println("over");
        return res;
    }

    public static void main(String[] args){
        final var inputNumber = inputNumber();
        EightNumberHelper.cal(inputNumber[0],inputNumber[1]);
    }
}
