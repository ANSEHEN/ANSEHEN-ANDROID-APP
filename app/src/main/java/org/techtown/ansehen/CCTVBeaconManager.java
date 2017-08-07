package org.techtown.ansehen;

public class CCTVBeaconManager{
    private String Array_CctvId[] = new String [20];
    int num;
    public void addCctvId(String temp){
        Array_CctvId[num++]=temp;
    }
    public void compareCctvId(String temp){
        int i;
        for(i=0;i<num;i++){
            if(temp.equals(Array_CctvId[i])){
                return;
            }
        }
        addCctvId(temp);
    }
}
