package examples.boot.demo1;

public class Node { //LinkedList에서 하나의 노드
    //하나의 노드는 데이터와 다음 노드의 참조값을 가짐
    int data;
    Node next = null;

    //노드의 데이터 초기화
    public Node(int d){
        this.data = d;
    }

    //노드 한개 추가
    public void append(int d){
        Node end = new Node(d);
        Node n = this;
        while(n.next != null){
            n = n.next;
        }
        n.next = end;
    }

    public void delete(int d){
        Node n = this;
        while(n.next != null){
            if(n.next.data == d){
                n.next = n.next.next;
            }else{
                n = n.next;
            }
        }
    }

    public void retrieve(){
        Node n = this;
        while(n.next != null){
            System.out.print(n.data+" -> ");
            n = n.next;
        }
        System.out.print(n.data);
    }
}

