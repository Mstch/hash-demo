import com.tiddar.hash.Circle;
import com.tiddar.hash.Node;
import com.tiddar.hash.Server;
import com.tiddar.hash.VirtualServer;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        map.put(1, 1);
        map.put(4, 4);
        map.put(2132131, 2132131);
        map.put(999, 999);
        System.out.println(map.entrySet());
        Node node = new Server();
        Server server = (Server) node;
        System.out.println((Server) node);
        server.vituralServers = new ArrayList<>();
        server.vituralServers.add(new VirtualServer());

        List<Node> nodes = server.vituralServers.stream().map(item -> (Node) item).collect(Collectors.toList());
        Circle circle = new Circle();
        Server server1 = new Server();
        server1.hash = 1;
        Server server2 = new Server();
        server2.hash = 2;
        Server server3 = new Server();
        server3.hash = 3;
        Server server4 = new Server();
        server4.hash = 4;
        Server server5 = new Server();
        server5.hash = 5;
        circle.register(server1);
        circle.register(server2);
        circle.register(server3);
        circle.register(server4);
        circle.register(server5);
        System.out.println(circle.getNodeMap());
    }
}
