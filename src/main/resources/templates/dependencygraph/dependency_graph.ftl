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
.modal {
  display: none; /* Hidden by default */
  position: fixed; /* Stay in place */
  z-index: 1; /* Sit on top */
  left: 0;
  top: 0;
  width: 100%; /* Full width */
  height: 100%; /* Full height */
  overflow: auto; /* Enable scroll if needed */
  background-color: rgb(0,0,0); /* Fallback color */
  background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
}

/* Modal Header */
.modal-header {
  padding: 2px 5px;
  background-color: #499ff5;
  color: white;
}

/* Modal Body */
.modal-body {padding: 2px 16px;}

/* Modal Footer */
.modal-footer {
  padding: 2px 5px;
  background-color: #499ff5;
  color: white;
}

/* Modal Content */
.modal-content {
  position: relative;
  background-color: #fefefe;
  margin: auto;
  padding: 0;
  border: 1px solid #888;
  width: 80%;
  box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19);
  animation-name: animatetop;
  animation-duration: 0.4s
}

/* Add Animation */
@keyframes animatetop {
  from {top: -300px; opacity: 0}
  to {top: 0; opacity: 1}
}

    </style>
</head>
<body>
<div class="header">
    <h2>Schema Dependencies</h2>
</div>
<div id="mynetwork">Schema Dependencies</div>
<div id="myModal" class="modal">
    <!-- Modal content -->
    <div class="modal-content">
        <div class="modal-header">
            <span class="close">&times;</span>
            <h2 id="fileName"></h2>
        </div>
        <div class="modal-body">
            <pre id="content"></pre>
        </div>
    </div>
</div>
<script type="text/javascript">
  // create an array with nodes
  var nodes = new vis.DataSet([
   <#list nodes as node>
       {id: ${node.id}, label: '${node.name}', shape: 'icon', size: 20, font : {size : 10},  icon: {face: 'FontAwesome',code: '\uf15b',size: 50,color: '#57169a'}, contents: ${node.contents}}<#sep>,
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

    network.on( 'click', function(properties) {
         var ids = properties.nodes;
         var clickedNodes = nodes.get(ids);
         var modal = document.getElementById("myModal");

     // Get the <span> element that closes the modal
     var span = document.getElementsByClassName("close")[0];

       modal.style.display = "block";
      var contents = JSON.stringify(clickedNodes[0].contents, null, '\t');
      document.getElementById("content").innerHTML = contents;
     // When the user clicks on <span> (x), close the modal
     span.onclick = function() {
       modal.style.display = "none";
     }
     // When the user clicks anywhere outside of the modal, close it
     window.onclick = function(event) {
       if (event.target == modal) {
         modal.style.display = "none";
       }
     }});

</script>
</body>
</html>