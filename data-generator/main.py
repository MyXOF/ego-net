from flask import Flask, render_template, request,  jsonify, abort,  make_response
import json, codecs
from config.config import *

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello World!'


@app.route('/test', methods=['GET'])
def test():
    return render_template("index.html")

@app.route("/node",methods=['GET','POST'])
def get_node():
    if request.method == 'POST':
        nodeId = request.json['nodeId']
    else:
        nodeId = request.args.get('nodeId', '')

    print(nodeId)
    return jsonify({'code':"OK"})
    pass

@app.route("/data", methods=['GET', 'POST'])
def get_data():
    nodes = json.load(codecs.open(NODE_DATA_PATH, 'r', 'utf-8-sig'))
    edges = json.load(codecs.open(EDGE_DATA_PATH, 'r', 'utf-8-sig'))
    # d = json.load(codecs.open('/Users/xuyi/Documents/workspace/ego-net/data-generator/data/json/relationship.json', 'r', 'utf-8-sig'))
    info = {}
    info['nodes'] = nodes['nodes']
    info['edges'] = edges['edges']
    return jsonify(info)
    pass

if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0')
