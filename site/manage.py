from flaskext.script import Manager
from urbanjungle import app
from os import system

manager = Manager(app)

@manager.command
def rebuild_db():
    ''' Drop and rebuild db from scratch '''
    system("db-migrate -c ./migrations_conf.py  --drop")

@manager.command
def load_data():
    ''' Load fixtures for project '''
    from urbanjungle.models.fixtures import load_fixtures
    load_fixtures()

if __name__ == "__main__":
    manager.run()
