import os
from flask import Flask

app = Flask(__name__)
if os.getenv('DEV') == 'yes':
    app.config.from_object('urbanjungle.config.DevelopmentConfig')
elif os.getenv('TEST') == 'yes':
    app.config.from_object('urbanjungle.config.TestConfig')
else:
    app.config.from_object('urbanjungle.config.ProductionConfig')

from urbanjungle.controllers.frontend import frontend
app.register_module(frontend)
