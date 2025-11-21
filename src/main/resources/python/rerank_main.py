from flask import Flask, request, jsonify
import requests

app = Flask(__name__)
url = "http://127.0.0.1:9997/v1/rerank"

# documents:["xxxx","xxxx","xxxx"]
def rerank(query, documents):
    payload = {
        "model": "bge-reranker-large",
        "query": query,
        "documents": list(documents)
    }
    headers = {
        "accept": "application/json",
        "Content-Type": "application/json"
    }
    response = requests.post(url, json=payload, headers=headers)
    return response.json()


@app.route('/rerank', methods=['POST'])
def rerank_api():
    try:
        data = request.json
        query = data.get('query')
        documents = data.get('documents')
        if not query or not documents:
            return jsonify({"error": "Invalid input"}), 400
        result = rerank(query, documents)
        return jsonify(result), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    app.run(debug=True, port=5001)
