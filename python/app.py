from flask import Flask, jsonify
import docker

app = Flask(__name__)
client = docker.from_env()

@app.route('/containers', methods=['GET'])
def list_containers():
    containers = client.containers.list()
    container_info = []
    for container in containers:
        container_info.append({
            'id': container.id,
            'name': container.name,
            'status': container.status,
            'image': container.image.tags
        })
    return jsonify(container_info)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
