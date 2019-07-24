package com.tiddar.hash;

import java.util.*;
import java.util.stream.Collectors;

public class Circle {
    private static final int CIRCLE_LENGTH = 100;
    private static float threshold = 2f;

    public TreeMap<Integer, Node> getNodeMap() {
        return nodeMap;
    }

    private TreeMap<Integer, Node> nodeMap = new TreeMap<>();


    public Node register(Server server) {
        Node nServer = nodeMap.put(server.getHash() % CIRCLE_LENGTH, server);
        //autoGenerateVirtualServer(server);
        return nServer;
    }

    public Node remove(Integer hashKey) {
        return nodeMap.remove(hashKey);
    }

    public Data getData(String key) {
        return nodeMap.higherEntry(key.hashCode()).getValue().get(key);
    }

    public Data setData(String key, Data data) {
        return nodeMap.higherEntry(key.hashCode()).getValue().put(key, data);
    }


    /**
     * 自动生成虚拟节点并计算负载
     *
     * @param nServer 新插入的server
     */
    public void autoGenerateVirtualServer(Server nServer) {
//        nodeMap.put(hashKey, nServer);
//        int avgLen = CIRCLE_LENGTH / nodeMap.size();
//        Map.Entry<Integer, Node> preNodeEntry = nodeMap.lowerEntry(hashKey);
//        Map.Entry<Integer, Node> nextNodeEntry = nodeMap.higherEntry(hashKey);
//        Map.Entry<Integer, Node> lastEntry = nodeMap.lastEntry();
//        Map.Entry<Integer, Node> firstEntry = nodeMap.firstEntry();
//        //计算新插入server的初始负载环长
//        int len = 0;
//        if (preNodeEntry == null) {
//            len += hashKey;
//            if (lastEntry == null) {
//                len = CIRCLE_LENGTH;
//            } else {
//                len += CIRCLE_LENGTH - lastEntry.getKey();
//            }
//        } else {
//            len = hashKey - preNodeEntry.getKey();
//        }
//
//        //当插入一个新节点时，下一个节点的负载环长会被影响，计算被影响的下一个server的新负载环长
//        int nextServerLen = 0;
//        if (nextNodeEntry == null) {
//            if (firstEntry != null) {
//                nextServerLen = firstEntry.getKey() + CIRCLE_LENGTH - hashKey;
//            }
//        } else {
//            nextServerLen = nextNodeEntry.getKey() - hashKey;
//        }


        nodeMap.put(nServer.getHash() % CIRCLE_LENGTH, nServer);
        HashMap<Integer, Integer> sizeMap = new HashMap<>();
        //计算各个节点的负载数量
        Map.Entry<Integer, Node> preNode = nodeMap.firstEntry();
        for (Map.Entry<Integer, Node> nodeEntry : nodeMap.entrySet()) {
            if (!nodeEntry.equals(nodeMap.firstEntry())) {
                if (nodeEntry.getValue() instanceof Server) {
                    sizeMap.put(nodeEntry.getKey(), nodeEntry.getKey() - preNode.getKey());
                } else if (nodeEntry.getValue() instanceof VirtualServer) {
                    VirtualServer virtualServer = ((VirtualServer) (nodeEntry.getValue()));
                    int len = nodeEntry.getKey() - preNode.getKey();
                    Server server = virtualServer.belongServer;
                    sizeMap.put(server.getHash(), sizeMap.get(server.getHash()) == null ? len : sizeMap.get(server.getHash()) + len);
                }
                preNode = nodeEntry;
            }
        }
        Map.Entry<Integer, Node> firstEntry = nodeMap.firstEntry();
        if (firstEntry.getValue() instanceof Server) {
            sizeMap.put(firstEntry.getKey(), CIRCLE_LENGTH - preNode.getKey() + firstEntry.getKey());
        } else if (firstEntry.getValue() instanceof VirtualServer) {
            VirtualServer virtualServer = ((VirtualServer) (firstEntry.getValue()));
            int len = CIRCLE_LENGTH - preNode.getKey() + firstEntry.getKey();
            Server server = virtualServer.belongServer;
            sizeMap.put(server.getHash(), sizeMap.get(server.getHash()) == null ? len : sizeMap.get(server.getHash()) + len);
        }

        TreeMap<Integer, Integer> sortedSizeKeyMap = new TreeMap<>();
        sizeMap.forEach((k, v) -> sortedSizeKeyMap.put(v, k));
        int minSize = sortedSizeKeyMap.firstKey();
        int maxSize = sortedSizeKeyMap.lastKey();
        Server maxSizeServer = (Server) nodeMap.get(sortedSizeKeyMap.lastEntry().getValue());
        Server minSizeServer = (Server) nodeMap.get(sortedSizeKeyMap.firstEntry().getValue());
        if (((float) maxSize) / minSize > threshold) {
            int thresholdMaxSize = (int) (minSize * threshold);
            //gap代表需要从max到min转化的负载
            int gap = (maxSize - thresholdMaxSize) / 2;
            int separated = 0;
            List<Node> nodes = maxSizeServer.vituralServers.parallelStream().map(item -> (Node) item).sorted(((o1, o2) -> Integer.compare(calculateNodeLoad(o2), calculateNodeLoad(o1)))).collect(Collectors.toList());
            nodes.add(maxSizeServer);
            Node node = nodes.get(0);
            if (calculateNodeLoad(node) > gap - separated) {
                int nHashIndex = preNode(node).getHash() % CIRCLE_LENGTH + gap - separated;
                VirtualServer virtualServer = new VirtualServer();
                virtualServer.belongServer = minSizeServer;
                minSizeServer.vituralServers.add(virtualServer);
                virtualServer.hash = nHashIndex;
                nodeMap.put(nHashIndex, virtualServer);
            }
        }
        //TODO 更合理的vitural Server插入方法
    }

    private Node preNode(Node node) {
        int hashKey = node.getHash() % CIRCLE_LENGTH;
        Map.Entry<Integer, Node> preNodeEntry = nodeMap.lowerEntry(hashKey);
        Map.Entry<Integer, Node> lastEntry = nodeMap.lastEntry();
        if (preNodeEntry == null) {
            return lastEntry.getValue();
        }
        return preNodeEntry.getValue();
    }

    private int calculateNodeLoad(Node node) {
        int hashKey = node.getHash() % CIRCLE_LENGTH;
        int len = 0;
        Map.Entry<Integer, Node> preNodeEntry = nodeMap.lowerEntry(hashKey);
        Map.Entry<Integer, Node> lastEntry = nodeMap.lastEntry();
        if (preNodeEntry == null) {
            len += hashKey;
            if (lastEntry == null) {
                len = CIRCLE_LENGTH;
            } else {
                len += CIRCLE_LENGTH - lastEntry.getKey();
            }
        } else {
            len = hashKey - preNodeEntry.getKey();
        }
        return len;
    }

}
