# Flask app

from flask import Flask, render_template, Response
import pycorda as pyc

app = Flask(__name__)
partyA = pyc.Node('jdbc:h2:tcp://localhost:55555/node','sa','')
node_info = partyA.get_node_infos().to_html()

@app.route('/')
def display_node_info():
    return Response(node_info)
