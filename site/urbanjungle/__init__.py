import os
from flask import Flask
from flaskext.babel import Babel

app = Flask(__name__)
if os.getenv('DEV') == 'yes':
    app.config.from_object('urbanjungle.config.DevelopmentConfig')
elif os.getenv('TEST') == 'yes':
    app.config.from_object('urbanjungle.config.TestConfig')
else:
    app.config.from_object('urbanjungle.config.ProductionConfig')

babel = Babel(app)

from urbanjungle.controllers.frontend import frontend
app.register_module(frontend)

from urbanjungle.controllers.backend import backend
app.register_module(backend, url_prefix='/admin')
