from dateutils import date

class Config(object):
    SECRET_KEY = '%\xe3\xc2\x8c\xff\x1c\x16\xf0\x82\xc9\x15\nG|e\x85[\x82\x19:\xb7\xb6\xf6h'
    ALLOWED_EXTENSIONS = set(['jpg', 'jpeg'])
    MAX_CONTENT_LENGTH = 2 * 1024 * 1024
    SQLALCHEMY_ECHO = False
    THUMB_WIDTH = 200
    THUMB_HEIGHT = 200

class ProductionConfig(Config):
    DEBUG = False
    TESTING = False
    UPLOAD_FOLDER = '/home/urbanjungle/reports/'
    THUMBS_FOLDER = '/home/urbanjungle/thumbs/'
    # Yeah, you see my password. So what?!
    SQLALCHEMY_DATABASE_URI = 'mysql://urbanjungle:zee5euSh@localhost/urbanjungle'

class TestConfig(Config):
    DEBUG = False
    TESTING = True

class DevelopmentConfig(Config):
    '''Use "if app.debug" anywhere in your code, that code will run in development code.'''
    DEBUG = True
    TESTING = True
    UPLOAD_FOLDER = '/tmp/upload/'
    THUMBS_FOLDER = '/tmp/upload/thumbs/'
    SQLALCHEMY_DATABASE_URI = 'sqlite:////tmp/test.db'
