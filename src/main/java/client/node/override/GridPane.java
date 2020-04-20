package client.node.override;

import javafx.scene.Node;

public class GridPane extends javafx.scene.layout.GridPane {
	public Node getNode(int col, int row) {
		for (Node node : this.getChildren()) {
			if (javafx.scene.layout.GridPane.getColumnIndex(node) == col && javafx.scene.layout.GridPane.getRowIndex(node) == row) {
				return node;
			}
		}
		return null;
	}
}
