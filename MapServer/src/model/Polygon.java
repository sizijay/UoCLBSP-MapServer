/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import Functions.DataBase;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public strictfp class Polygon {
 
    
    private int id; 
    private List<Vertex> vertexes;
    private List<Edge> edges;

    public Polygon(int id, List<Vertex> vertexes, List<Edge> edges) {
        this.id = id;
        this.vertexes = vertexes;
        this.edges = edges;
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    //Function to add Vertex to the Graph. Returns the added Vertex, if the Vertex exists returns it. Returns null if unsucceeded. 
    public Vertex addVertex(double latitude, double longitude) {
        int currid = -1;
        for (Vertex vertex : vertexes) {
            if (vertex.getLatitude() == latitude && vertex.getLongitude() == longitude) {
                return vertex;
            }
        }
        try {
            Statement statement = DataBase.createStatement();
            statement.executeUpdate("INSERT INTO p_vertex(latitudes,longitudes,graph_id) VALUES('"+latitude+"','"+longitude+"','"+id+"') ");
            ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID() FROM p_vertex");
            rs.next();
            currid = rs.getInt(1); 
            Vertex newVertex = new Vertex(currid, latitude, longitude);
            vertexes.add( newVertex);
            return newVertex;
            
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return null;
    }
    
    public Edge addEdge(Vertex v1 , Vertex v2){
        int currid = -1;
        for (Edge edge : edges) {
            if (edge.getSource().equals(v1) && edge.getDestination().equals(v2)) {
                return edge;
            }
            if (edge.getSource().equals(v2) && edge.getDestination().equals(v1)) {
                return edge;
            }
        }
        try {
            Statement statement = DataBase.createStatement();
            statement.executeUpdate("INSERT INTO p_edge(p_vertex_id,p_vertex_id1) VALUES('"+v1.getId()+"','"+v2.getId()+"') ");
            ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID() FROM p_edge");
            rs.next();
            currid = rs.getInt(1); 
            Edge newEdge = new Edge(currid, v1, v2, 0);
            edges.add(newEdge);
            return newEdge;
            
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return null;
    }
    
    public JSONObject getMap(){
        JSONObject map = new JSONObject();
        JSONArray elements = new JSONArray();
        for (Vertex element : vertexes) {
            JSONObject jo = new JSONObject();
            jo.put("id",element.getId());
            jo.put( "latitude",element.getLatitude());
            jo.put("longitude",element.getLongitude() );
            elements.add(jo);
        }
        map.put("vertexes",elements);
        elements = new JSONArray();
        for (Edge element : edges) {
            JSONObject jo = new JSONObject();
            jo.put("id",element.getId() );
            jo.put("source",element.getSource().getId() );
            jo.put("destination",element.getDestination().getId() );
            elements.add(jo);
        }
        map.put("edges",elements);
        
        return map;
    
    }
    
    public Vertex searchVertex(double latitudes, double longitudes){
        double minimum = Double.MAX_VALUE;
        Vertex minVertex = null;
        for (Vertex vertex : vertexes) {
            double x = vertex.getLatitude()-latitudes;
            double y = vertex.getLongitude()-longitudes;
            double distance = Math.sqrt((x*x)+(y*y));
            if(minimum>distance){
                minimum = distance;
                minVertex = vertex;
                if (minimum==0) {
                    return minVertex;
                }
            }
        }
        return minVertex;
    }
    
    public Vertex searchVertex(int id){
        for (Vertex vertex : vertexes) {
            if (vertex.getId()==id) {
                return vertex;
            }
        }
        return null;
    }
    
    public Edge searchEdge(int id){
        for (Edge edge : edges) {
            if (edge.getId()== id) {
                return edge;
            }
        }
        return null;
    
    }
    
    
    



}
