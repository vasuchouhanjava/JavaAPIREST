package salesforce_rest;

public class Main {

    public static void main(String[] args) {
        String n1 = "abcdefghijklmnopqrstuvwxyz";
        String n2 = "the quick Brow fox jumps over lazy dog";

        n2 = n2.replace(" ","");

        n1 = n1.toLowerCase();
        n2 = n2.toLowerCase();

        String[] arr1 = n1.split("");
        String[] arr2 = n2.split("");

        int arrlen = arr1.length;
        int n = 0;

        for(int i = 0; i < arr1.length; i++ ){
            for(int j = 0; j < arr2.length; j++ ){
                if(arr1[i].equals(arr2[j])){
                    n++;
                    break;
                }
                else{
                }
            }
        }

        if(n==arrlen){
            System.out.println("String is Anagram");
        }
        else{
            System.out.println("String is not Anagram");
        }




    }
}