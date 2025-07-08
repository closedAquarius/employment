import os
import cv2
import face_recognition
import numpy as np
import base64
import re
from flask import Flask, request, jsonify
from flask_cors import CORS
import logging
import time

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = Flask(__name__)
# 启用CORS支持，明确允许来自localhost:3000的请求
CORS(app, resources={r"/*": {"origins": ["http://localhost:3000", "http://127.0.0.1:3000"]}})

# 创建存储人脸的目录
KNOWN_FACES_DIR = 'known_faces'
os.makedirs(KNOWN_FACES_DIR, exist_ok=True)

def preprocess_base64_image(base64_image):
    """预处理base64图像数据"""
    try:
        # 检查并处理可能的data:image前缀
        if ',' in base64_image:
            logger.info("Decoded base64 image with comma")
            base64_image = base64_image.split(',', 1)[1]
        
        # 解码base64图像
        image_data = base64.b64decode(base64_image)
        
        # 将图像数据转换为numpy数组
        nparr = np.frombuffer(image_data, np.uint8)
        
        # 解码为OpenCV图像
        image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        if image is None:
            logger.error("Failed to decode image")
            return None
            
        logger.info(f"Decoded image shape: {image.shape}")
        
        # 检查图像是否太小
        if image.shape[0] < 100 or image.shape[1] < 100:
            logger.warning(f"Image too small: {image.shape}")
            return None
            
        # 保存调试图像
        debug_dir = "debug_images"
        os.makedirs(debug_dir, exist_ok=True)
        debug_path = os.path.join(debug_dir, f"debug_{int(time.time())}.jpg")
        cv2.imwrite(debug_path, image)
        logger.info(f"Saved debug image to: {debug_path}")
        
        return image
    except Exception as e:
        logger.error(f"Error preprocessing image: {str(e)}")
        return None
def load_known_faces(user_id):
    """加载已知的人脸编码"""
    known_face_encodings = []
    known_face_names = []
    
    user_dir = os.path.join(KNOWN_FACES_DIR, str(user_id))
    logger.info(f"Loading known faces from path: {user_dir}")
    
    if not os.path.exists(user_dir):
        logger.info(f"User directory does not exist: {user_dir}")
        os.makedirs(user_dir, exist_ok=True)
        return known_face_encodings, known_face_names
    
    files = os.listdir(user_dir)
    logger.info(f"Files in directory: {files}")
    
    for file in files:
        if file.endswith('.jpg') or file.endswith('.png'):
            image_path = os.path.join(user_dir, file)
            try:
                # 加载图像
                face_image = cv2.imread(image_path)
                logger.info(f"Loading face from: {image_path}")
                
                if face_image is None:
                    logger.error(f"Failed to load image: {image_path}")
                    continue
                    
                logger.info(f"Image shape: {face_image.shape}")
                
                # 转换为RGB (face_recognition使用RGB)
                rgb_image = cv2.cvtColor(face_image, cv2.COLOR_BGR2RGB)
                
                # 检测人脸
                face_locations = face_recognition.face_locations(rgb_image)
                
                if not face_locations:
                    logger.warning(f"No faces found in image: {image_path}")
                    continue
                
                # 提取人脸编码
                face_encoding = face_recognition.face_encodings(rgb_image, face_locations)[0]
                known_face_encodings.append(face_encoding)
                known_face_names.append(str(user_id))
                logger.info(f"Added encoding for user: {user_id}")
            except Exception as e:
                logger.error(f"Error processing {image_path}: {str(e)}")
    
    logger.info(f"Loaded {len(known_face_encodings)} known faces")
    return known_face_encodings, known_face_names

@app.route('/verify', methods=['POST'])
def verify():
    """验证人脸"""
    try:
        data = request.json
        if not data:
            return jsonify({"success": False, "status": "1", "message": "No data provided"}), 400
        
        user_id = data.get('userId')
        base64_image = data.get('image')
        
        if not user_id or not base64_image:
            return jsonify({"success": False, "status": "1", "message": "Missing userId or image"}), 400
        
        logger.info(f"Received verify request for user: {user_id}")
        logger.info(f"Starting face verification with image size: {len(base64_image)}")
        
        # 加载已知人脸
        known_face_encodings, known_face_names = load_known_faces(user_id)
        logger.info(f"Number of known faces: {len(known_face_encodings)}")
        
        if not known_face_encodings:
            return jsonify({
                "success": False,
                "status": "1",
                "message": "No registered faces found for comparison"
            })
        
        # 处理上传的图像
        image = preprocess_base64_image(base64_image)
        if image is None:
            return jsonify({"success": False, "status": "1", "message": "Invalid image data"}), 400
        
        # 转换为RGB
        rgb_image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        
        # 检测人脸
        face_locations = face_recognition.face_locations(rgb_image)
        logger.info(f"Detected {len(face_locations)} faces")
        
        if not face_locations:
            return jsonify({
                "success": False,
                "status": "1", 
                "message": "No face detected in the image"
            })
        
        # 获取人脸编码
        face_encoding = face_recognition.face_encodings(rgb_image, face_locations)[0]
        logger.info(f"Generated face encoding with length: {len(face_encoding)}")
        
        # 比较人脸
        matches = []
        for i, known_encoding in enumerate(known_face_encodings):
            # 比较人脸，使用较低的容差以提高准确性
            match = face_recognition.compare_faces([known_encoding], face_encoding, tolerance=0.5)[0]
            name = known_face_names[i]
            logger.info(f"Comparing with known user: {name}")
            logger.info(f"Match result for user {name}: {match}")
            if match:
                matches.append(name)
        
        if matches:
            # 找到匹配
            return jsonify({
                "success": True,
                "status": "0",
                "message": f"Verification successful: The user is {user_id}",
                "userid": user_id
            })
        else:
            # 没有匹配
            return jsonify({
                "success": False,
                "status": "1",
                "message": "Face verification failed: No matching face found"
            })
            
    except Exception as e:
        logger.error(f"Error during verification: {str(e)}")
        return jsonify({
            "success": False,
            "status": "1",
            "message": f"Verification error: {str(e)}"
        }), 500

@app.route('/register-face', methods=['POST'])
def register_face():
    """注册人脸"""
    try:
        data = request.json
        if not data:
            return jsonify({"success": False, "message": "No data provided"}), 400
        
        user_id = data.get('userId')
        base64_image = data.get('image')
        
        if not user_id or not base64_image:
            return jsonify({"success": False, "message": "Missing userId or image"}), 400
        
        logger.info(f"Received register-face request for user: {user_id}")
        logger.info(f"Image data length: {len(base64_image)}")
        
        # 确保用户目录存在
        user_dir = os.path.join(KNOWN_FACES_DIR, str(user_id))
        os.makedirs(user_dir, exist_ok=True)
        
        # 处理上传的图像
        image = preprocess_base64_image(base64_image)
        if image is None:
            return jsonify({"success": False, "message": "Invalid image data"}), 400
        
        # 转换为RGB
        rgb_image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        
        # 尝试多种人脸检测方法
        face_locations = face_recognition.face_locations(rgb_image, model="hog")
        logger.info(f"HOG face detector found {len(face_locations)} faces")
        
        # 如果HOG检测器没找到人脸，尝试CNN检测器（更准确但更慢）
        if not face_locations and os.path.exists("models/cnn"):
            try:
                logger.info("Trying CNN face detector")
                face_locations = face_recognition.face_locations(rgb_image, model="cnn")
                logger.info(f"CNN face detector found {len(face_locations)} faces")
            except Exception as e:
                logger.error(f"CNN face detection failed: {str(e)}")
        
        # 如果仍然没找到人脸，尝试OpenCV的Haar级联检测器
        if not face_locations:
            logger.info("Trying OpenCV Haar cascade face detector")
            face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
            cv_faces = face_cascade.detectMultiScale(gray, 1.1, 4)
            
            # 将OpenCV格式转换为face_recognition格式
            if len(cv_faces) > 0:
                face_locations = []
                for (x, y, w, h) in cv_faces:
                    face_locations.append((y, x + w, y + h, x))
                logger.info(f"OpenCV face detector found {len(face_locations)} faces")
        
        if not face_locations:
            return jsonify({
                "success": False, 
                "message": "No face detected in the image"
            })
        
        # 保存图像
        logger.info(f"Saving face for user: {user_id}")
        image_path = os.path.join(user_dir, f"{user_id}.jpg")
        cv2.imwrite(image_path, image)
        logger.info(f"Saving image to: {image_path}")
        
        # 验证保存的图像是否可以检测到人脸
        saved_image = cv2.imread(image_path)
        if saved_image is None:
            logger.error(f"Failed to read saved image: {image_path}")
            return jsonify({
                "success": False,
                "message": "Failed to save face image"
            })
            
        saved_rgb = cv2.cvtColor(saved_image, cv2.COLOR_BGR2RGB)
        saved_faces = face_recognition.face_locations(saved_rgb)
        logger.info(f"Verified saved image contains {len(saved_faces)} faces")
        
        if not saved_faces:
            logger.warning("No face detected in saved image, but proceeding anyway")
        
        return jsonify({
            "success": True,
            "message": "Face registered successfully"
        })
        
    except Exception as e:
        logger.error(f"Error during face registration: {str(e)}")
        return jsonify({
            "success": False,
            "message": f"Registration error: {str(e)}"
        }), 500

@app.route("/health", methods=["GET"])
def health_check():
    return jsonify({"status": "ok"})

@app.route("/webrtc/offer", methods=["POST"])
def webrtc_offer():
    """处理WebRTC offer请求"""
    try:
        data = request.json
        if not data:
            return jsonify({"error": "No data provided"}), 400
        
        webrtc_id = data.get('webrtc_id')
        sdp = data.get('sdp')
        offer_type = data.get('type')
        
        logger.info(f"Received WebRTC offer from: {webrtc_id}")
        
        # 创建响应SDP
        response = {
            "sdp": sdp,  # 简单回显SDP，实际应用中应该处理并生成适当的answer SDP
            "type": "answer"
        }
        
        return jsonify(response)
        
    except Exception as e:
        logger.error(f"Error processing WebRTC offer: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route("/input_hook", methods=["POST"])
def input_hook():
    """处理输入事件"""
    try:
        data = request.json
        if not data:
            return jsonify({"error": "No data provided"}), 400
        
        webrtc_id = data.get('webrtc_id')
        message_type = data.get('type', 'unknown')
        
        if message_type == 'message':
            message = data.get('message', '')
            logger.info(f"Received message from {webrtc_id}: {message}")
        elif 'personality' in data:
            personality = data.get('personality')
            logger.info(f"Received personality choice from {webrtc_id}: {personality}")
        
        # 返回成功响应
        return jsonify({"success": True})
        
    except Exception as e:
        logger.error(f"Error processing input hook: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route("/outputs", methods=["GET"])
def outputs():
    """处理SSE连接的端点"""
    webrtc_id = request.args.get('webrtc_id')
    
    if not webrtc_id:
        return "No webrtc_id provided", 400
    
    # 使用SSE格式返回响应
    def generate():
        yield "data: {\"type\": \"connected\", \"message\": \"WebRTC connected\"}\n\n"
        # 在实际应用中，这里应该持续发送消息直到连接关闭
    
    logger.info(f"Established SSE connection for {webrtc_id}")
    return app.response_class(generate(), mimetype='text/event-stream')

if __name__ == '__main__':
    print(f"Starting face recognition service on port 5001...")
    app.run(host='0.0.0.0', port=5001)
