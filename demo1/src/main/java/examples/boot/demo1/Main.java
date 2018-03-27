package examples.boot.demo1;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int num;
        while(true){
            num = sc.nextInt();
            if(num>=1 && num<=20000)
                break;
        }
        sc.next();
        String word;
        String[] str = new String[num];
        for(int i=0;i<num;i++){
            while(true){
                word = sc.next();
                if(word.length()<=50)
                    str[i]=word;
                    break;
            }
        }
        String tmp;
        for(int i=0;i<num-1;i++){
            for(int j=0;j<num-1-i;j++){
                if(str[j].length()>str[j+1].length()){
                   tmp = str[j+1];
                   str[j+1] = str[j];
                   str[j] = tmp;
                }
                if(str[j].length()==str[j+1].length()){
                    if(str[j].compareTo(str[j+1])==1){
                        tmp = str[j+1];
                        str[j+1] = str[j];
                        str[j] = tmp;
                    }
                }
            }
        }
        for(String e : str){
            System.out.print(e+" ");
        }
    }
}
