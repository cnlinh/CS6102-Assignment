package application;
	
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class Main extends Application {
	private Stage stage;
	private StackPane root, nodeBP;
	private HBox buttonBP;
	private VBox tableBP;
	private ComboBox cb1,cb2;
	private TextField costTF;
	private Label error, init, fin, inProgress;
	private TableView<TableData> initTable, finalTable;
	private ArrayList<TableView<TableData>> tableList;
	private ArrayList<Button> nodes;
	private ArrayList<Button> buttons;
	private ArrayList<Line> edges;
	private ArrayList<Node> nodeList;
	private ArrayList<Edge> edgeList;
	private ArrayList<ObservableList<TableData>> tableUpdates;
	private ArrayList<Animation> animations;
	private ArrayList<String> updateNodes;
	private final int WIDTH = 1000, HEIGHT = 660;
	private double nodeWidth, nodeHeight;
	private int count=0;//number of nodes -1
	private double width;
	private double height;
	private String currentNode="";
	private static ArrayList<String> alphabet = new ArrayList<String>(Arrays.asList("abcdefghijklmnopqrstuvwxyz".toUpperCase().split("")));
	
	@Override
	public void start(Stage stage) {
		try {
			root = new StackPane();
			Scene scene = new Scene(root,WIDTH,HEIGHT);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			
			initialize();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void initialize() {
		//adds the pane with all the nodes and edges
		nodeBP=new StackPane();
		StackPane.setAlignment(nodeBP, Pos.BOTTOM_LEFT);
		nodeBP.setMinSize(WIDTH*3/5, WIDTH*3/5);
		nodeBP.setPrefSize(WIDTH*3/5, WIDTH*3/5);
		nodeBP.setMaxSize(WIDTH*3/5, WIDTH*3/5);
		nodeBP.setStyle("-fx-background-color: #FF0000;");
		root.getChildren().add(nodeBP);
		
		//adds the first node 'A'
		Button a=new Button();
		a.setText("A");
		a.setOnAction(e->nodeClick(e));
		nodeBP.getChildren().add(a);
		count=0;
		
		//an arraylist with all the nodes
		nodes=new ArrayList<Button>();
		nodes.add(a);
		edges=new ArrayList<Line>();
		
		//adds a pane at the top with add/remove buttons
		buttonBP = new HBox();
		buttonBP.setMinSize(WIDTH, HEIGHT-(WIDTH*3/5));
		buttonBP.setPrefSize(WIDTH, HEIGHT-(WIDTH*3/5));
		buttonBP.setMaxSize(WIDTH, HEIGHT-(WIDTH*3/5));
		buttonBP.setStyle("-fx-background-color: #00FF00;");

		StackPane.setAlignment(buttonBP, Pos.TOP_LEFT);
		root.getChildren().add(buttonBP);
		
		//adds a pane on the right with the distance tables
		tableBP=new VBox();
		tableBP.setMinSize(WIDTH*2/5, WIDTH*3/5);
		tableBP.setPrefSize(WIDTH*2/5, WIDTH*3/5);
		tableBP.setMaxSize(WIDTH*2/5, WIDTH*3/5);
		tableBP.setStyle("-fx-background-color: #0000FF;");
		
		HBox tables=new HBox();
		
		//initial dist vector label and table
		VBox table1=new VBox();
		table1.setMinWidth(WIDTH/5-20);
		table1.setPrefWidth(WIDTH/5-20);
		table1.setMaxWidth(WIDTH/5-20);
		HBox.setMargin(table1, new Insets(10,10,10,10));
		
		init=new Label("Initial Distance\nVector");
		VBox.setMargin(init, new Insets(0,0,10,0));
		init.setStyle("-fx-text-fill: black;-fx-font-weight: bold;-fx-font-size: 18");
		init.setTextAlignment(TextAlignment.CENTER);
		initTable=new TableView<TableData>();
		TableColumn initC1=new TableColumn("Node");
		TableColumn initC2=new TableColumn("Cost");
		initC1.setCellValueFactory(
                new PropertyValueFactory<TableData, String>("node"));
		initC2.setCellValueFactory(
                new PropertyValueFactory<TableData, String>("cost"));
		initTable.getColumns().add(initC1);
		initTable.getColumns().add(initC2);
		table1.setAlignment(Pos.CENTER);
		table1.getChildren().add(init);
		table1.getChildren().add(initTable);
		
		//final dist vector label and table
		VBox table2=new VBox();
		table2.setMinWidth(WIDTH/5-20);
		table2.setPrefWidth(WIDTH/5-20);
		table2.setMaxWidth(WIDTH/5-20);
		HBox.setMargin(table2, new Insets(10,10,10,10));
		
		fin=new Label("Final Distance\nVector");
		VBox.setMargin(fin, new Insets(0,0,10,0));
		fin.setStyle("-fx-text-fill: black;-fx-font-weight: bold;-fx-font-size: 18");
		fin.setTextAlignment(TextAlignment.CENTER);
		finalTable=new TableView<TableData>();
		TableColumn finalC1=new TableColumn("Node");
		TableColumn finalC2=new TableColumn("Cost");
		finalC1.setCellValueFactory(
                new PropertyValueFactory<TableData, String>("node"));
		finalC2.setCellValueFactory(
                new PropertyValueFactory<TableData, String>("cost"));
		finalTable.getColumns().add(finalC1);
		finalTable.getColumns().add(finalC2);
		table2.setAlignment(Pos.CENTER);
		table2.getChildren().add(fin);
		table2.getChildren().add(finalTable);
		
		tables.getChildren().add(table1);
		tables.getChildren().add(table2);
		
		inProgress=new Label("");
		inProgress.setVisible(false);
		inProgress.setStyle("-fx-text-fill: black;-fx-font-weight: bold;-fx-font-size: 16");
		tableBP.setAlignment(Pos.TOP_CENTER);
		
		Button update=new Button("Evaluate Final Vector");
		update.setDisable(true);
		update.setOnAction(e->updateFinalTable());
	
		VBox updateSection=new VBox(inProgress,update);
		VBox.setMargin(update, new Insets(10,10,10,10));
		updateSection.setAlignment(Pos.BOTTOM_CENTER);
		
		tableBP.getChildren().add(tables);
		tableBP.getChildren().add(updateSection);
		
		StackPane.setAlignment(tableBP, Pos.BOTTOM_RIGHT);
		root.getChildren().add(tableBP);
		
		tableList = new ArrayList<TableView<TableData>>();
		tableList.add(initTable);
		tableList.add(finalTable);
		
		//Add the add/remove node buttons to top pane
		Button addNode=new Button("Add Node");
		addNode.setOnAction(e->addNode());
		Button removeNode=new Button("Remove Node");
		removeNode.setOnAction(e->removeNode());
		
		HBox nodeHB=new HBox(addNode,removeNode);
		nodeHB.setAlignment(Pos.CENTER);
		HBox.setMargin(addNode, new Insets(10,10,10,10));
		HBox.setMargin(removeNode, new Insets(10,30,10,0));
		StackPane.setAlignment(nodeHB, Pos.CENTER_LEFT);
		buttonBP.getChildren().add(nodeHB);
		
		Text add = new Text("between");
		Text and = new Text("and");
		Text with = new Text("with cost =");
		
		cb1 = new ComboBox();
		cb1.getItems().add("A");
		cb2 = new ComboBox();
		cb2.getItems().add("A");
		cb1.setValue("A");
		cb2.setValue("A");
		
		Button addEdge = new Button("Add/Remove Path");
		addEdge.setOnAction(e->addRemoveEdge());
		
		costTF=new TextField();
		costTF.setMaxWidth(50);
		
		HBox edgeHB=new HBox(addEdge,add,cb1,and,cb2,with,costTF);
		edgeHB.setAlignment(Pos.CENTER);
		HBox.setMargin(add, new Insets(10,10,10,10));
		HBox.setMargin(and, new Insets(10,0,10,0));
		HBox.setMargin(with, new Insets(10,0,10,0));
		HBox.setMargin(cb1, new Insets(10,10,10,0));
		HBox.setMargin(cb2, new Insets(10,10,10,10));
		HBox.setMargin(cb2, new Insets(10,10,10,10));
		HBox.setMargin(costTF, new Insets(10,10,10,10));
		buttonBP.getChildren().add(edgeHB);
		
		error=new Label("");
		error.setStyle("-fx-text-fill: red;-fx-font-weight: bold");
		buttonBP.setAlignment(Pos.CENTER_LEFT);
		HBox.setMargin(error, new Insets(10,10,10,30));
		buttonBP.getChildren().add(error);
		
		nodeList=new ArrayList<Node>();
		nodeList.add(new Node("A"));
		edgeList=new ArrayList<Edge>();
		
		//list of all buttons except nodes, so can deactivate them when updating
		buttons=new ArrayList<Button>();
		buttons.add(addNode);
		buttons.add(removeNode);
		buttons.add(addEdge);
		buttons.add(update);
		
		redrawNodes();
	}
	
	private void removeNode() {
		if(count<=0) {
			return;//do nothing if only have 1 node
		}
		--count;
		nodeList.remove(nodeList.size()-1);
		nodeBP.getChildren().remove(nodes.get(nodes.size()-1));
		nodes.remove(nodes.size()-1);
		redrawNodes();
		updateCB();
		//update positions of edges
				for(int i=0; i<edges.size();++i) {
					nodeBP.getChildren().remove(edges.get(i));
				}
				edges.clear();
				String a,b;
				for(int i=0; i<edgeList.size();++i) {
					a=edgeList.get(i).getA();
					b=edgeList.get(i).getB();
					if(alphabet.indexOf(a)>=nodes.size() || alphabet.indexOf(b)>=nodes.size()) {
						edgeList.remove(i);
						--i;
						continue;
					}
					drawEdge(a,b);
				}
		//reset selection if selected node is deleted
		if(alphabet.indexOf(currentNode)>count) {
			currentNode="";
			setTableData(currentNode,0);
			setTableData(currentNode,1);
			buttons.get(3).setDisable(true);
		}
		//re-select selected node to update edge colour and table data
		if(!currentNode.equals("")) {
			nodes.get(alphabet.indexOf(currentNode)).fire();
		}
	}
	public void addNode() {
		if(count>=25) {
			return;//do nothing if already got 26 nodes
		}
		count++;
		nodeList.add(new Node(alphabet.get(count)));
		Button b = new Button(alphabet.get(count));
		b.setOnAction(e->nodeClick(e));
		nodes.add(b);
		nodeBP.getChildren().add(b);
		redrawNodes();
		updateCB();
		//update positions of edges
				for(int i=0; i<edges.size();++i) {
					nodeBP.getChildren().remove(edges.get(i));
				}
				edges.clear();
				for(int i=0; i<edgeList.size();++i) {
					drawEdge(edgeList.get(i).getA(),edgeList.get(i).getB());
				}
				
				//reselect selected node to update edge color and table data
				if(!currentNode.equals("")) {
					nodes.get(alphabet.indexOf(currentNode)).fire();
				}
	}
	//called when node is added/removed
	private void redrawNodes() {
		width=nodes.get(0).getWidth();
		height=nodes.get(0).getHeight();
		double centerX=WIDTH*3/10 - width/2;
		double centerY=WIDTH*3/10 - height/2;
		double radius=WIDTH*3/12;
		Button b;
		//add back the updated nodes in new position
		if(count==0) {
			b=nodes.get(0);
			StackPane.setAlignment(b, Pos.CENTER);
			b.setLayoutX(centerX);
			b.setLayoutY(centerY);
			b.setTranslateX(0);
			b.setTranslateY(0);
			return;
		}
		for(int i=0, n=nodes.size();i<n;++i) {
			b=nodes.get(i);
			StackPane.setAlignment(b, Pos.TOP_LEFT);
			b.setLayoutX(centerX + radius*Math.sin(2*Math.PI*i/n));
			b.setLayoutY(centerY - radius*Math.cos(2*Math.PI*i/n));
			b.setTranslateX(centerX + radius*Math.sin(2*Math.PI*i/n));
			b.setTranslateY(centerY - radius*Math.cos(2*Math.PI*i/n));
		}
	}
	public void updateCB() {
		cb1.getItems().clear();
		cb2.getItems().clear();
		for(int i=0; i<count+1;++i) {
			cb1.getItems().add(alphabet.get(i));
			cb2.getItems().add(alphabet.get(i));
		}
		cb1.setValue("A");
		cb2.setValue("A");
	}
	//called when add/remove edge button is clicked
	//gives error msg if cost is not positive integer or both nodes are the same
	public void addRemoveEdge() {
		String text=costTF.getText();
		int cost=0;
		if(text==null || !text.matches("\\d+")) {
			error.setTextFill(Color.RED);
			error.setText("Cost must be a positive integer!");
			return;
		}
		cost=Integer.parseInt(text);
		if(cost<=0) {
			error.setTextFill(Color.RED);
			error.setText("Cost must be a positive integer!");
			return;
		}
		String a=(String) cb1.getValue();
		String b=(String) cb2.getValue();
		if(a.equals(b)) {
			error.setVisible(true);
			error.setTextFill(Color.RED);
			error.setText("Cannot add path between same node!");
			return;
		}
		Edge e;
		for(int i=0; i<edgeList.size(); ++i) {
			e=edgeList.get(i);
			if(a.equals(e.getA()) && b.equals(e.getB())){
				removeEdge(a,b,e);
				return;
			}
			else if(a.equals(e.getB()) && b.equals(e.getA())) {
				removeEdge(a,b,e);
				return;
			}
		}
		addEdge(a,b,cost);
	}
	public void addEdge(String a, String b, int cost) {
		edgeList.add(new Edge(a,b,cost));
		drawEdge(a,b);
		
		//reselect selected node to update edge color and table data
		if(!currentNode.equals("")) {
			nodes.get(alphabet.indexOf(currentNode)).fire();
		}
		
		error.setText("Path between "+a+" and "+b+" added! Cost: "+cost);
		error.setTextFill(Color.GREEN);
	}
	public void drawEdge(String a, String b) {
		Line l = new Line();
		StackPane.setAlignment(l, Pos.CENTER);
		Button start=nodes.get(alphabet.indexOf(a));
		Button end=nodes.get(alphabet.indexOf(b));
		double lwidth=end.getTranslateX()-start.getTranslateX();
		double lheight=end.getTranslateY()-start.getTranslateY();
		
		l.setTranslateX(start.getTranslateX()+lwidth/2+width/2-WIDTH*3/10);
		l.setTranslateY(start.getTranslateY()+lheight/2+width/2-WIDTH*3/10);
		l.setStartX(0);
		l.setStartY(0);
		l.setEndX(lwidth);
		l.setEndY(lheight);
		l.setStrokeWidth(2);
		
		edges.add(l);
		nodeBP.getChildren().add(l);
		
		l.toBack();
	}
	public void removeEdge(String a, String b, Edge e) {
		nodeBP.getChildren().remove(edges.get(edgeList.indexOf(e)));
		edges.remove(edgeList.indexOf(e));
		edgeList.remove(e);
		//reselect selected node to update edge color and table data
		if(!currentNode.equals("")) {
			nodes.get(alphabet.indexOf(currentNode)).fire();
		}
		error.setText("Path between "+a+" and "+b+" successfully removed!");
		error.setTextFill(Color.GREEN);
	}
	//when a node is clicked
	public void nodeClick(ActionEvent ae) {
		String s=((Button)ae.getSource()).getText();
		currentNode=s;
		Edge e;
		ArrayList<Edge> adjacentEdges=new ArrayList<Edge>();
		for(int i=0; i<edges.size(); ++i) {
			e=edgeList.get(i);
			if(s.equals(e.getA()) || s.equals(e.getB())) {
				adjacentEdges.add(e);
				edges.get(i).setStroke(Color.web("#00FF00"));
			}
			else {
				edges.get(i).setStroke(Color.web("#888888"));
			}
		}
		setTableData(s,0);
		setTableData("",1);
		buttons.get(3).setDisable(false);
		inProgress.setVisible(false);
	}
	//set the values in the table (0>left, 1>right) given the name of a node
	//if s is "" then clear the table and disable update button
	public Node setTableData(String s, int tableNum) {
		if(s.equals("")) {
			buttons.get(3).setDisable(true);
			tableList.get(tableNum).getItems().clear();
			if(tableNum==0)
				init.setText("Initial Distance\nVector");
			else
				fin.setText("Final Distance\nVector");
			return null;
		}
		buttons.get(3).setDisable(false);
		Edge e;
		ArrayList<Edge> adjacentEdges=new ArrayList<Edge>();
		for(int i=0; i<edges.size(); ++i) {
			e=edgeList.get(i);
			if(s.equals(e.getA()) || s.equals(e.getB())) {
				adjacentEdges.add(e);
			}
		}
		Node n=nodeList.get(alphabet.indexOf(s));
		n.initialize(adjacentEdges, count);
		ArrayList<String> col1=n.getNodes();
		ArrayList<Integer> col2=n.getCost();
		ObservableList<TableData> data=FXCollections.observableArrayList();
		for(int i=0;i<col1.size();++i) {
			data.add(new TableData(col1.get(i), col2.get(i)+""));
		}
		tableList.get(tableNum).setItems(data);
		if(tableNum==0)
			init.setText("Initial Distance\nVector of "+s);
		else
			fin.setText("Final Distance\nVector of "+s);
		return n;
	}
	
	//when update button is pressed, update the initial distance vector
	public void updateFinalTable() {
		//disable all buttons first
		for(Button b:nodes) {
			b.setDisable(true);
		}
		for(Button b:buttons) {
			b.setDisable(true);
		}
		buttons.get(3).setVisible(false);
		
		setTableData(currentNode, 0);
		setTableData(currentNode, 1);
		
		//keep track of visited nodes
		ArrayList<String> visited=new ArrayList<String>();
		ArrayList<String> unvisited=new ArrayList<String>();
		for(int i=0;i<=count;++i) {
			if(alphabet.get(i).equals(currentNode)) {
				visited.add(currentNode);
				continue;
			}
			unvisited.add(alphabet.get(i));
		}
		
		//get data of selected node
		Node n=nodeList.get(alphabet.indexOf(currentNode));
		ArrayList<String> nodes=n.getNodes();
		ArrayList<Integer> costs=n.getCost();
		
		inProgress.setTextFill(Color.WHITE);
		
		tableUpdates=new ArrayList<ObservableList<TableData>>();
		animations=new ArrayList<Animation>();
		updateNodes=new ArrayList<String>();
		String s="";
		Node adj;
		int cost=0;
		boolean repeat=false;//if there is an update, run through all nodes again
		for(int i=0; i<=nodes.size(); ++i) {
			if(i==nodes.size()) {
				if(repeat) {
					repeat=false;
					i=0;
				}
				else {
					break;
				}
			}
			s=nodes.get(i);
			if(visited.contains(s)) {//already visited that node
				continue;
			}
			cost=costs.get(i);
			if(cost<0) {//inaccessible node
				continue;
			}
			//if it reaches here, there is an update
			repeat=true;
			visited.add(s);
			unvisited.remove(s);
			//fill the right table with data of an adjacent node
			adj=setTableData(s,0);
			//get data of adjacent node that has not been visited
			ArrayList<String> col1=adj.getNodes();
			ArrayList<Integer> col2=adj.getCost();
			
			System.out.println(s);
			for(int j=0;j<col1.size();++j) {
				System.out.println(col1.get(j)+" - "+col2.get(j));
			}
			
			for(int j=0; j<nodes.size(); ++j) {
				//cost of adj node to node j is inf, so keep original cost
				if(col2.get(j)<0) {
					continue;
				}
				//cost of current node to node j is inf, so use path through adj node
				if(costs.get(j)<0) {
					costs.remove(j);
					costs.add(j, cost+col2.get(j));
					continue;
				}
				//cost of either route is defined, so use min cost path
				int temp=costs.get(j);
				costs.remove(j);
				costs.add(j, Math.min(temp, cost+col2.get(j)));
			}
			System.out.println(currentNode+" after adding "+s);
			for(int j=0;j<costs.size();++j) {
				System.out.println(nodes.get(j)+" - "+costs.get(j));
			}
			
			ObservableList<TableData> data=FXCollections.observableArrayList();
			for(int j=0;j<nodes.size();++j) {
				data.add(new TableData(nodes.get(j), costs.get(j)+""));
			}
			RotateTransition rt=new RotateTransition();
			rt.setNode(error);
			rt.setFromAngle(0);
			rt.setToAngle(0);
			rt.setDelay(Duration.millis(3000));
			rt.setDuration(Duration.millis(1));
			rt.setOnFinished(e->{
				animations.remove(rt);
				if(!animations.isEmpty()) {
					inProgress.setVisible(true);
					inProgress.setText("Updating "+currentNode+" using "+updateNodes.get(0)+"...");
					init.setText("Initial Distance\nVector of "+updateNodes.get(0));
					updateNodes.remove(0);
					//set the right table data as the updated current node
					tableList.get(1).setItems(tableUpdates.get(0));
					tableUpdates.remove(0);
		
					//set the left table data as the node to be processed
					tableList.get(0).setItems(tableUpdates.get(0));
					tableUpdates.remove(0);
					animations.get(0).play();
				}
				else {//once the last animation has played
					inProgress.setVisible(true);
					inProgress.setTextFill(Color.LIME);
					inProgress.setText("Update Completed!");
					for(Button b: buttons) {
						b.setDisable(false);
					}
					for(Button b: this.nodes) {
						b.setDisable(false);
					}
					buttons.get(3).setVisible(true);
					init.setText("Initial Disance\nVector of "+currentNode);
					//set the left table data as initial distance vector
					setTableData(currentNode,0);
					//set the right table data as final distance vector
					tableList.get(1).setItems(tableUpdates.get(0));
				}
			});
			animations.add(rt);
			tableUpdates.add(tableList.get(0).getItems());
			tableUpdates.add(data);
			updateNodes.add(s);
		}
		//once all updates are done, animate through them one by one
		if(!animations.isEmpty()) {
			//initially set the left table data as the first node giving the updates
			tableList.get(0).setItems(tableUpdates.get(0));
			init.setText("Initial Distance\nVector of "+updateNodes.get(0));
			inProgress.setVisible(true);
			inProgress.setText("Updating "+currentNode+" using "+updateNodes.get(0)+"...");
			
			tableUpdates.remove(0);
			updateNodes.remove(0);
			//initially set the right table data as initial dist vector
			setTableData(currentNode, 1);
			animations.get(0).play();
		}
		else {
			for(Button b: buttons) {
				b.setDisable(false);
			}
			for(Button b: this.nodes) {
				b.setDisable(false);
			}
			buttons.get(3).setVisible(true);
			inProgress.setTextFill(Color.LIME);
			inProgress.setText("Update Completed!");
			inProgress.setVisible(true);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	static class Node{
		private String name;
		private ArrayList<String> nodes;
		private ArrayList<Integer> cost;
		Node (String name){
			this.name=name;
			nodes=new ArrayList<String>();
			cost=new ArrayList<Integer>();
		}
		public void initialize(ArrayList<Edge> edges, int count) {
			nodes.clear();
			cost.clear();
			
			//add paths to neighbours
			for(int i=0; i<=count;++i) {
				//looks for an edge connecting it with each other node
				//if don't have, put cost to -1
				for(int j=0;j<edges.size();++j) {
					Edge e=edges.get(j);
					if(e.getA().equals(name) && e.getB().equals(alphabet.get(i))) {
						nodes.add(e.getB());
						cost.add(e.getCost());
						break;
					}
					else if(e.getB().equals(name) && e.getA().equals(alphabet.get(i))) {
						nodes.add(e.getA());
						cost.add(e.getCost());
						break;
					}
					if(j==edges.size()-1) {
						if(name.equals(alphabet.get(i))) {
							nodes.add(name);
							cost.add(0);
						}
						else {
							nodes.add(alphabet.get(i));
							cost.add(-1);
						}
					}
				}
			}
			if(nodes.isEmpty()) {
				for(int i=0; i<=count;++i) {
					nodes.add(alphabet.get(i));
					if(alphabet.get(i).equals(name))
						cost.add(0);
					else
						cost.add(-1);
				}
			}
		}
		public void update(Node n) {
			
		}
		public String getName() {
			return name;
		}
		public ArrayList<String> getNodes(){
			return nodes;
		}
		public ArrayList<Integer> getCost(){
			return cost;
		}
	}
	static class Edge {
		private String a,b;
		private int cost;
		Edge (String a, String b, int cost){
			this.a=a;
			this.b=b;
			this.cost=cost;
		}
		public int getCost() {
			return cost;
		}
		public void setCost(int cost) {
			this.cost=cost;
		}
		public String getA() {
			return a;
		}
		public String getB() {
			return b;
		}
	}
	public static class TableData{
		private String node;
		private String cost;
		public TableData(String node,String cost){
			this.setNode(node);
			this.setCost(cost);
			if(cost.equals("-1"))
				this.setCost("inf");
		}
		public String getNode() {
			return node;
		}
		public void setNode(String node) {
			this.node = node;
		}
		public String getCost() {
			return cost;
		}
		public void setCost(String cost) {
			this.cost = cost;
		}
	}
}
