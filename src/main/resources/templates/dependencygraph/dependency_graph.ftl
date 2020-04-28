<!doctype html>
<html>
<head>
    <title>Dependencies</title>
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <script type="text/javascript" src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"></script>
    <style type="text/css">
    #mynetwork {
      width: 100%;
      height: 800px;
      border: 1px solid lightgray;
    }
    body {
  margin: 25px;
  background-color: rgb(240,240,240);
  font-family: arial, sans-serif;
  font-size: 14px;
}
.header {
  padding: 10px;
  text-align: center;
  background: #499ff5;
  color: white;
  font-size: 15px;
}
</style>
</head>
<body>
<div class="header">
    <h2>Schema Dependencies</h2>
</div>
<div id="mynetwork">Schema Dependencies</div>
<script type="text/javascript">
  // create an array with nodes
  var nodes = new vis.DataSet([
   <#list nodes as node>
       {id: ${node.id}, label: '${node.name}', shape: 'icon', size: 20, font : {size : 10},  icon: {face: 'FontAwesome',code: '\uf15b',size: 50,color: '#57169a'}}<#sep>,
   </#list>
  ]);

  // create an array with edges
  var edges = new vis.DataSet([
    <#list edges as edge>
    <#if edge.inherits>
     {from: ${edge.from}, to: ${edge.to}, label: '${edge.label}', dashes: true, arrows: 'from', font : {size : 10}}<#sep>,
    <#else>
     {from: ${edge.from}, to: ${edge.to}, label: '${edge.label}', dashes: false, arrows: 'from', font : {size : 10}}<#sep>,
    </#if>
    </#list>
  ]);

  // create a network
  var container = document.getElementById('mynetwork');
  var data = {
    nodes: nodes,
    edges: edges
  };
  var options = {};
  var network = new vis.Network(container, data, options);

</script>
</body>
</html>