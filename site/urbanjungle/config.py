from dateutils import date

class Config(object):
    SECRET_KEY = '%\xe3\xc2\x8c\xff\x1c\x16\xf0\x82\xc9\x15\nG|e\x85[\x82\x19:\xb7\xb6\xf6h'

class ProductionConfig(Config):
    DEBUG = False
    TESTING = False

class TestConfig(Config):
    DEBUG = False
    TESTING = True

class DevelopmentConfig(Config):
    '''Use "if app.debug" anywhere in your code, that code will run in development code.'''
    DEBUG = True
    TESTING = True
