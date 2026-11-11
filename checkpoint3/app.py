from flask import Flask, jsonify, request, redirect, send_from_directory
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import set_access_cookies, create_access_token, JWTManager, jwt_required, get_jwt_identity, unset_jwt_cookies, decode_token
from flask_cors import CORS
from flask_admin import Admin
from flask_admin.contrib.sqla import ModelView #viewclass for sqlaclhemy
from flask_admin.base import MenuLink
from dotenv import load_dotenv
from flask_bcrypt import Bcrypt
import os
from flask_socketio import SocketIO, join_room, leave_room, send, emit
import random, string

# compied from cse 108 project, these are the ones we used
#pip install flask-jwt-extended
#pip install flask-cors
#pip install python-dotenv
#pip install flask_sqlalchemy
#pip install flask_bcrypt
#pip install flask-socketio
#pip install gevent
#pip install simple-websocket

app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///example.sqlite"
db = SQLAlchemy(app)
CORS(app, supports_credentials=True, origins=["http://localhost:5173"])

admin = Admin(app, name="Grades Admin")
#Key will be utilized from .env file
load_dotenv()
app.config["SECRET_KEY"] = os.getenv("REGULAR_SECRET") #our internal Flask-Admin secret key, we need both or else it throws a runtime error when we try to create users and likely more
app.config["JWT_SECRET_KEY"] = os.getenv("JWT_SECRET") # store in secrets manager
app.config["JWT_TOKEN_LOCATION"] = ["cookies"]
app.config["JWT_COOKIE_SECURE"] = False # True in production
app.config["JWT_COOKIE_CSRF_PROTECT"] = True
jwt = JWTManager(app)
