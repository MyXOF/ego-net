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


@app.route("/data", methods=['GET', 'POST'])
def get_data():
    d = json.load(codecs.open(JSON_DATA_PATH + 'data-generator/data/json/relationship.json', 'r', 'utf-8-sig'))
    return jsonify(d)
    pass

if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0')
