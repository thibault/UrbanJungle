from urbanjungle.models import db
from urbanjungle.models.report import Report

db.metadata.bind = db.engine

def reload_everything():
    db.metadata.drop_all()
    db.metadata.create_all()
    load_fixtures()

def load_fixtures():
    load_report_fixtures()

def load_report_fixtures():
    # Repalatin, Montpellier
    r = Report(51.86653,0.006959)
    db.session.add(r)

    # UM2, Montpellier
    r = Report(43.609948,3.87414)
    db.session.add(r)

    db.session.commit()
