from urbanjungle.models import db
from datetime import datetime

class Report(db.Model):
    __tablename__ = 'reports'
    id = db.Column(db.Integer, primary_key=True)
    latitude = db.Column(db.DECIMAL(9,6))
    longitude = db.Column(db.DECIMAL(9,6))
    created = db.Column(db.DateTime, default=datetime.now())

    def __init__(self, latitude, longitude):
        self.latitude = latitude
        self.longitude = longitude

    def __repr__(self):
        return '<Report(%s,%s)>' % (self.latitude, self.longitude)

    def __json__(self):
        return { 'latitude': '%9.6f' % self.latitude, 'longitude': '%9.6f' % self.longitude }
