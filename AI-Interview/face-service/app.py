from flask import Flask, request, jsonify
import face_recognition
import numpy as np
import cv2
import base64
import os
from flask_cors import CORS
import logging

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)  # 启用CORS，允许前端跨域请求

# 存储已知用户信息的目录
KNOWN_FACES_DIR = "known_faces"

def load_known_faces(user_id):
    logger.info(f"Loading known faces for user_id: {user_id}")
    if user_id:
        path = os.path.join(KNOWN_FACES_DIR, str(user_id))
    else:
        path = os.path.join(KNOWN_FACES_DIR, "admin")
    
    known_faces = {}
    if not os.path.exists(path):
        logger.info(f"Creating directory: {path}")
        os.makedirs(path)
    
    logger.info(f"Checking directory: {path}")
    files = os.listdir(path)
    logger.info(f"Files in directory: {files}")
    
    for filename in files:
        if filename.endswith(".jpg") or filename.endswith(".png"):
            name = os.path.splitext(filename)[0]
            image_path = os.path.join(path, filename)
            logger.info(f"Loading face from: {image_path}")
            
            try:
                image = face_recognition.load_image_file(image_path)
                encodings = face_recognition.face_encodings(image)
                
                if encodings:
                    if user_id:
                        known_faces[str(user_id)] = encodings[0]
                        logger.info(f"Successfully loaded face for user: {user_id}")
                    else:
                        known_faces[name] = encodings[0]
                        logger.info(f"Successfully loaded face for user: {name}")
                else:
                    logger.warning(f"No face encodings found in image: {image_path}")
            except Exception as e:
                logger.error(f"Error loading face from {image_path}: {str(e)}")
    
    logger.info(f"Loaded {len(known_faces)} known faces")
    return known_faces

def verify_face(image_base64, known_faces):
    try:
        logger.info("Starting face verification")
        # 解码Base64图像
        image_data = base64.b64decode(image_base64.split(",")[1])
        nparr = np.frombuffer(image_data, np.uint8)
        image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        logger.info(f"Decoded image shape: {image.shape}")

        # 提取人脸特征
        face_locations = face_recognition.face_locations(image)
        logger.info(f"Detected {len(face_locations)} faces")
        
        if len(face_locations) == 0:
            logger.warning("No face detected in the image")
            return {"status": "1", "message": "No face detected"}

        face_encoding = face_recognition.face_encodings(image, face_locations)[0]

        # 与已知用户比对
        for user_id, known_encoding in known_faces.items():
            logger.info(f"Comparing with known user: {user_id}")
            match = face_recognition.compare_faces([known_encoding], face_encoding, tolerance=0.5)
            if match[0]:
                logger.info(f"Match found for user: {user_id}")
                return {"status": "0", "message": f"Verification successful: The user is {user_id}", "userid": user_id}

        logger.warning("No matching face found")
        return {"status": "1", "message": "Verification failed: The user is not recognized"}
    except Exception as e:
        logger.error(f"Error in verify_face: {str(e)}", exc_info=True)
        return {"status": "1", "message": f"Error processing image: {str(e)}"}

def save_face(image_base64, user_id):
    try:
        logger.info(f"Saving face for user: {user_id}")
        # 解码Base64图像
        image_data = base64.b64decode(image_base64.split(",")[1])
        nparr = np.frombuffer(image_data, np.uint8)
        image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        logger.info(f"Decoded image shape: {image.shape}")

        # 保存图像到已知用户目录
        user_dir = os.path.join(KNOWN_FACES_DIR, user_id)
        if not os.path.exists(user_dir):
            logger.info(f"Creating directory: {user_dir}")
            os.makedirs(user_dir)
        image_path = os.path.join(user_dir, f"{user_id}.jpg")
        logger.info(f"Saving image to: {image_path}")
        cv2.imwrite(image_path, image)

        return {"status": "0", "message": f"Face saved for user {user_id}"}
    except Exception as e:
        logger.error(f"Error in save_face: {str(e)}", exc_info=True)
        return {"status": "1", "message": f"Error saving face: {str(e)}"}

@app.route("/register-face", methods=["POST"])
def register_face():
    try:
        logger.info("Received register-face request")
        data = request.json
        logger.info(f"Request data: {data.keys()}")
        image_base64 = data.get("image")
        user_id = data.get("userId")
        
        if not image_base64 or not user_id:
            logger.warning("Missing image or userId in request")
            return jsonify({"status": "1", "message": "Missing image or userId"}), 400
            
        result = save_face(image_base64, user_id)
        logger.info(f"Registration result: {result}")
        return jsonify(result)
    except Exception as e:
        logger.error(f"Error in register_face endpoint: {str(e)}", exc_info=True)
        return jsonify({"status": "1", "message": f"Server error: {str(e)}"}), 500

@app.route("/verify-face", methods=["POST"])
def verify_face_endpoint():
    try:
        logger.info("Received verify-face request")
        data = request.json
        logger.info(f"Request data: {data.keys()}")
        image_base64 = data.get("image")
        user_id = data.get("userId")
        
        if not image_base64:
            logger.warning("Missing image in request")
            return jsonify({"status": "1", "message": "Missing image"}), 200
            
        known_faces = load_known_faces(user_id)
        
        if not known_faces:
            logger.warning(f"No registered face found for user {user_id}")
            return jsonify({"status": "1", "message": f"No registered face found for user {user_id}"}), 200
            
        result = verify_face(image_base64, known_faces)
        logger.info(f"Verification result: {result}")
        return jsonify(result), 200
    except Exception as e:
        logger.error(f"Error in verify_face endpoint: {str(e)}", exc_info=True)
        return jsonify({"status": "1", "message": f"Server error: {str(e)}"}), 200

@app.route("/health", methods=["GET"])
def health_check():
    logger.info("Health check requested")
    return jsonify({"status": "UP", "message": "Face recognition service is running"})

if __name__ == "__main__":
    # 确保存储目录存在
    if not os.path.exists(KNOWN_FACES_DIR):
        os.makedirs(KNOWN_FACES_DIR)
    
    logger.info("Starting face recognition service on port 5001...")
    app.run(host="0.0.0.0", port=5001, debug=True) 