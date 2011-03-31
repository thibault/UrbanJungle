import os
from flask import Module, request, current_app, render_template, jsonify
from werkzeug import secure_filename
from urbanjungle.models import db
from urbanjungle.models.report import Report
from sqlalchemy.ext.serializer import dumps

frontend = Module(__name__)

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in current_app.config['ALLOWED_EXTENSIONS']

@frontend.route('/report/<latitude>,<longitude>', methods=['GET', 'PUT'])
def upload(latitude, longitude):
    if request.method == 'PUT':
        file = request.files['file']
        if file and allowed_file(file.filename):

            r = Report(latitude, longitude)
            db.session.add(r)
            db.session.commit()

            filename = '%s.jpg' % r.id

            file.save(os.path.join(current_app.config['UPLOAD_FOLDER'], filename))
            return ''
        else:
            return 'File not allowed', 403
    else:
        return '''
            <!doctype html>
            <title>Upload new File</title>
            <h1>Upload new File</h1>
            <form action="" method="put" enctype=multipart/form-data>
            <p><input type=file name=file>
            <input type=submit value=Upload>
            </form>
        '''

@frontend.route('/map')
def map():
    return render_template('map.html')

@frontend.route('/map/markers/<ne_lat>,<ne_lng>,<sw_lat>,<sw_lng>.json')
def get_markers(ne_lat, ne_lng, sw_lat, sw_lng):
    markers = Report.query \
        .filter(Report.latitude < ne_lat) \
        .filter(Report.latitude > sw_lat) \
        .filter(Report.longitude < ne_lng) \
        .filter(Report.longitude > sw_lng) \
        .all()

    json_markers = { 'markers' : [ marker.__json__() for marker in markers ] }
    return jsonify(json_markers)
