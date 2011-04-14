from urbanjungle import app
from urlparse import urlparse

uri = urlparse(app.config['SQLALCHEMY_DATABASE_URI'])

DATABASE_HOST = uri.hostname
DATABASE_USER = uri.username
DATABASE_PASSWORD = uri.password
DATABASE_NAME = uri.path.lstrip('/')
DATABASE_MIGRATIONS_DIR='./migrations'
