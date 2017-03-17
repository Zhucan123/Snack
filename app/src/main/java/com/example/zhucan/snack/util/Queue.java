package com.example.zhucan.snack.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhucan on 2017/3/13.
 */


public class Queue{
    Coord first;
    Coord end;
    int length;
    public Queue(){
        first=null;
        end=null;
        length=0;
    }

    public void insert(int x,int y){
        Coord newCoord=new Coord(x,y);
        if (length==0){
           end=newCoord;
            first=newCoord;
        }else if (length==1){
            newCoord.next=first;
            first=newCoord;
            end.head=newCoord;
        }else {
            first.head=newCoord;
            newCoord.next=first;
            first=newCoord;
        }

        length++;
    }

    public Coord deleteEnd(){
        Coord temp=end.head;
        temp.next=null;
        length--;
        return temp;
    }


    public Coord getFirst(){
        return first;
    }

    public List<Coord> showAll(){
        List<Coord> show = new ArrayList<>();
        Coord current=first;
        if (!isEmpty()) {
           for (int i=0;i<length;i++){
               show.add(current);
               current=current.next;
           }
        }
        return show;
    }

    public int getLength(){
        return length;
    }

    public boolean isEmpty(){
        return (first==null);
    }

}
