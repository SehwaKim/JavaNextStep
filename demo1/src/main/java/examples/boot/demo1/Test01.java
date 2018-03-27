package examples.boot.demo1;

public class Test01 {
    public static void main(String[] args) {
        int[] arr=new int[]{5,4,3,2,1};
        int tmp;
        for(int i=0;i<arr.length-1;i++){
            for(int j=1;j<arr.length-i;j++){
                if(arr[j-1]>arr[j]){
                    tmp=arr[j-1];
                    arr[j-1]=arr[j];
                    arr[j]=tmp;
                }
            }
            for(int e : arr){
                System.out.print(e+" ");
            }
            System.out.println("");
        }
    }
}
