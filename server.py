from flask import Flask, jsonify
from flask import request

app = Flask(__name__)

totalnums = []

@app.route('/', methods=['GET', 'POST'])
def add():
    content = request.get_data()
    print content
    content = request.get_json()
    print content
    if "a" in content and "b" in content:
        num = content["a"] + content["b"]
        return jsonify({"Answer": num})


@app.route('/callback', methods=['POST'])
def cb():
    content = request.get_json()
    print content
    totalnums.append(content)
    return jsonify({"Success":True})

@app.route('/callback', methods=['GET'])
def listcbs():
    return jsonify(totalnums)



if __name__ == '__main__':
    app.run(debug=True)
