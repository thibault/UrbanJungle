#!/usr/bin/env python
from migrate.versioning.shell import main
from urbanjungle import app


main(url=app.config['SQLALCHEMY_DATABASE_URI'], debug=app.config['DEBUG'], repository='migrations/')
