import java.util.*;
import java.lang.Math;
/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
  //Creating the root node.
  private DecTreeNode root;
  //Make a copy of the root node.
  private DecTreeNode root_copy;
  //ordered list of class labels.
  private List<String> labels; 
  //ordered list of attributes.
  private List<String> attributes;
  //ordered list of instances.
  private List<Instance> instances;  
  //map to ordered discrete values taken by attributes.
  private Map<String, List<String>> attributeValues; 
  //map for getting the index.
  private HashMap<String,Integer> attr_inv;  
  /**
   * Answers static questions about decision trees.
   */
  DecisionTreeImpl() {
    // no code necessary this is void purposefully
  }
  /**
   * Build a decision tree given only a training set.
   * 
   * @param train: the training set
   */
  DecisionTreeImpl(DataSet train) {
	this.labels = train.labels;
	this.attributes = train.attributes;
	this.attributeValues = train.attributeValues;
	this.instances = train.instances;
	//Initializing the root and calling the recursive function.
	root = builttree(instances, attributes, null, majorityLabel(instances));
	//Making a copy of the root node.
	    root_copy = root;
	  }  
	    private DecTreeNode builttree(List<Instance> instances, List<String> attribute, String parentAttributeValue, String label){
	    	//Defining the base cases.
		if(instances.size() == 0) return new DecTreeNode(label, null, parentAttributeValue, true);
		if(sameLabel(instances) == true) return new DecTreeNode(instances.get(0).label, null, parentAttributeValue, true);
		if(attribute.size()==0) return new DecTreeNode(majorityLabel(instances), null, parentAttributeValue, true);
		//Determining the best attribute.
		String best_attribute = "";
	    double IG, IG_max;
	    IG = 0;
	    IG_max = -1;
	    for(String attribute_instance: attribute){
	    	IG = InfoGain(instances, attribute_instance);
	    	if(IG > IG_max){
	    		IG_max = IG;
	    		best_attribute = attribute_instance;
	    	}    	
	    }
	  //Defining the tree.
		DecTreeNode tree = new DecTreeNode(label, best_attribute, parentAttributeValue, false);
		//Loop for each value of the attribute.
		for (String attribute_instance: attributeValues.get(best_attribute)){
			    //Subset of instances with best_attribute value equal to attribute_instance.			
				List<Instance> newList = new ArrayList<Instance>();
				for (int i=0; i<instances.size(); i++){
					if(instances.get(i).attributes.get(getAttributeIndex(best_attribute)).equals(attribute_instance)){
						newList.add(instances.get(i));
					}    				
				}
				//Attributes minus best_attributes.
				List<String> Attributes_minus_best_attribute = new ArrayList<String>(attribute);
				for (Iterator<String> iter = Attributes_minus_best_attribute.listIterator(); iter.hasNext();){
	    		    String attributes_removal = iter.next();
	    		    if (attributes_removal.equals(best_attribute)) {
	    		        iter.remove();
	    		    }
				}
				//Building the subtree.
		    	DecTreeNode subtree = builttree(newList, Attributes_minus_best_attribute, attribute_instance, majorityLabel(instances));
		    	//Adding the children to the tree.
		    	tree.addChild(subtree);    		    		
		}
		return tree;
    }  
  boolean sameLabel(List<Instance> instances){
      // Suggested helper function
      // Returns true if all the instances have the same label
	  String good = labels.get(0);
	  int positive, negative;
	  positive = negative = 0;
	  //Counting the number of positives and negative in the data.
	  for (int i=0; i<instances.size(); i++){
		  if(instances.get(i).label.equals(good)){
			  positive = positive + 1;
		  }
		  else{negative = negative + 1;
		  }
	  }
	//Checking if all of them are either positive or negative.
	  if (positive == instances.size() || negative == instances.size()){
		  return true;
	  }
	  else{return false;
	  }
  }
  String majorityLabel(List<Instance> instances){
      // Suggested helper function
      // Returns the majority label of a list of examples
	  String good = labels.get(0);
	  String bad = labels.get(1);
	  int positive, negative;
	  positive = negative = 0;
	//Counting the number of positives and negative in the data.
	  for (int i=0; i<instances.size(); i++){ 
		  if(instances.get(i).label.equals(good)){
			  positive = positive + 1;
		  }
		  else{negative = negative + 1;
		  }
	  }
	  //Determining the majority labels.
	  if (positive >= negative){
		  return good;
	  }
	  else{return bad;
	  }
  }
  double entropy(List<Instance> instances){
	  // Suggested helper function
      // Returns the entropy of the data.
	  String good = labels.get(0);
	  int positive, negative;
	  positive = negative = 0;
	  double entropy_value;
	  //Counting the number of positives and negative in the data.
	  for (int i=0; i<instances.size(); i++){
		  if(instances.get(i).label.equals(good)){
			  positive = positive + 1;
		  }
		  else{negative = negative + 1;
		  }
	  }
	  double p1, p2;
	  //Determining the entropy of the data using the log formula.
	  p1 = positive/(double)instances.size();
	  p2 = negative/(double)instances.size();
	  entropy_value = -(p1)*(Math.log(p1)/Math.log(2))-(p2)*(Math.log(p2)/Math.log(2));
	  return entropy_value;
  }
  double conditionalEntropy(List<Instance> instances, String attr){
	  // Suggested helper function
      // Returns the conditional entropy of the data.
	  String good = labels.get(0);
	  int positive, negative;
	  positive = negative = 0;
	  double ConditionalEntropy_value, SpecificConditionalEntropy_value, p1, p2, sum;
	  ConditionalEntropy_value = SpecificConditionalEntropy_value = p1 = p2 = 0;
	  for (String attribute_instance: attributeValues.get(attr)){
		  sum = positive = negative = 0;
		  SpecificConditionalEntropy_value = 0;
		  //Finding the number of positive and negative instances.
		  for (int i=0; i<instances.size(); i++){
			  if(instances.get(i).attributes.get(getAttributeIndex(attr)).equals(attribute_instance)){
				  if(instances.get(i).label.equals(good)){
					  positive = positive + 1;
				  }
				  else{
					  negative = negative + 1;
				  }
			  }
		  }
		  //Determining the count of the data.
		  sum = positive + negative;
		  //Determining the probabilities.
		  if(sum != 0){
			  p1 = positive/sum;
			  p2 = negative/sum;  
		  }
		  else{p1 = p2 = 0;
		  }	
		  //Determining the specific conditional entropy.
		  if (p1 != 0 && p2 != 0){
			  SpecificConditionalEntropy_value = SpecificConditionalEntropy_value -(p1)*(Math.log(p1)/Math.log(2))-(p2)*(Math.log(p2)/Math.log(2));
		  }
		  else if (p1 != 0 && p2 == 0){
			  SpecificConditionalEntropy_value = SpecificConditionalEntropy_value -(p1)*(Math.log(p1)/Math.log(2));
		  }
		  //Determining the conditional entropy.
		  ConditionalEntropy_value = ConditionalEntropy_value + (sum/(double)instances.size())*SpecificConditionalEntropy_value;
	  }
	  //Returning the conditional entropy value.
      return ConditionalEntropy_value;
  }
  double InfoGain(List<Instance> instances, String attr){
      // Returns the info gain of a list of examples, given the attribute attr.
      return entropy(instances) - conditionalEntropy(instances,attr);
  }
  @Override 
  public String classify(Instance instance) {
	  //Terminal case for the leaf.
	  if(this.root.terminal == true){
		  return this.root.label;
	  }
	  String node_tree, value_instance;
	  node_tree = value_instance = "";
	  int index_node = 0;
	  //node_tree finds out the root of the learned decision tree.
	  node_tree = this.root.attribute;
	  //index_node finds out the index of the node_tree in the attribute list.
	  index_node = attributes.indexOf(node_tree);
	  //value instance gives the value corresponding to the selected node from the data.
	  value_instance = instance.attributes.get(index_node);
	  //Traversing down the tree.
	  this.root = this.root.children.get(getAttributeValueIndex(node_tree, value_instance));
	  String result_label = "";
	  result_label = classify(instance);
	  //Replenishing the root node.
	  root = root_copy;
	  //Returning the label.
      return result_label;
  }  
  @Override
  public void rootInfoGain(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    this.instances = train.instances;
    double IG = 0;
    //Printing the information gain.
    for (String attribute_instance: attributes){
    	IG = InfoGain(instances, attribute_instance);
    	System.out.format("%s %.5f\n", attribute_instance, IG);
    }
  }
  @Override
  public void printAccuracy(DataSet test) {
	  int correct_prediction = 0;
	  double accuracy = 0;
	  //Calling each instance in the test case.
	  for (int i=0; i<test.instances.size(); i++){
		  //Checking if the predicted label is same actual label.
		  if(classify(test.instances.get(i)).equals(test.instances.get(i).label)){
			  //If the predicted label is equal to the actual label, increment the correct prediction count.
			  correct_prediction+=1;
		  }
	  }
	  //Determining the accuracy.
	  accuracy = correct_prediction/(double)test.instances.size();
	  //Printing the accuracy.
	  System.out.format("%.5f\n", accuracy);
    return;
  }  
  @Override
  /**
   * Print the decision tree in the specified format
   * Do not modify
   */
  public void print() {

    printTreeNode(root, null, 0);
  }
  /**
   * Prints the subtree of the node with each line prefixed by 4 * k spaces.
   * Do not modify
   */
  public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append("    ");
    }
    String value;
    if (parent == null) {
      value = "ROOT";
    } else {
      int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
      value = attributeValues.get(parent.attribute).get(attributeValueIndex);
    }
    sb.append(value);
    if (p.terminal) {
      sb.append(" (" + p.label + ")");
      System.out.println(sb.toString());
    } else {
      sb.append(" {" + p.attribute + "?}");
      System.out.println(sb.toString());
      for (DecTreeNode child : p.children) {
        printTreeNode(child, p, k + 1);
      }
    }
  }
  /**
   * Helper function to get the index of the attribute in attributes list
   */
  private int getAttributeIndex(String attr) {
    if(attr_inv == null)
    {
        this.attr_inv = new HashMap<String,Integer>();
        for(int i=0; i < attributes.size();i++)
        {
            attr_inv.put(attributes.get(i),i);
        }
    }
    return attr_inv.get(attr);
  }
  /**
   * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
   */
  private int getAttributeValueIndex(String attr, String value) {
    for (int i = 0; i < attributeValues.get(attr).size(); i++) {
      if (value.equals(attributeValues.get(attr).get(i))) {
        return i;
      }
    }
    return -1;
  }
}