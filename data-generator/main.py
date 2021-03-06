from flask import Flask, render_template, request,  jsonify, redirect
from pylib.Manager import NodeManager

app = Flask(__name__)


@app.route('/')
def go_to_index():
    return redirect("/index")


@app.route('/index', methods=['GET'])
def index():
    return render_template("index.html")

@app.route("/node",methods=['GET','POST'])
def get_node():
    if request.method == 'POST':
        nodeId = request.json['nodeId']
        opr_type = request.json['type']
    else:
        nodeId = request.args.get('nodeId', '')
        opr_type = request.args.get('type', '')
    return jsonify(NodeManager.get_ego_net(nodeId, opr_type))
    pass

@app.route("/graph", methods=['GET', 'POST'])
def get_data():
    NodeManager.clear_ego_net()
    return jsonify(NodeManager.get_graph())
    pass

if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0')
