import os
import Image
from flask import Module, request, current_app, render_template, jsonify, send_file, abort
from werkzeug import secure_filename
from urbanjungle.models import db
from urbanjungle.models.report import Report
from sqlalchemy.ext.serializer import dumps

frontend = Module(__name__)

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in current_app.config['ALLOWED_EXTENSIONS']

@frontend.route('/report/<latitude>,<longitude>', methods=['GET', 'PUT'])
def upload(latitude, longitude):
    '''Handle file upload'''
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
            abort(403)
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

@frontend.route('/report/thumbnail/<report_id>.jpg')
def generate_thumbnail(report_id):
    '''
    Generate thumbnail for given image.
    This uri should be passed through flask only if the thumb file does not exists.
    Otherwise, it should be served as a static file.
    '''
    image_path = os.path.join(current_app.config['UPLOAD_FOLDER'], report_id + '.jpg')
    thumb_path = os.path.join(current_app.config['THUMBS_FOLDER'], report_id + '.jpg')

    if '..' in image_path or not os.path.exists(image_path):
        abort(404)

    if not os.path.exists(thumb_path):
        image = Image.open(image_path)
        image.thumbnail((current_app.config['THUMB_WIDTH'], current_app.config['THUMB_HEIGHT']), \
            Image.ANTIALIAS)
        image.save(thumb_path)

    return send_file(thumb_path, mimetype="image/jpeg")

@frontend.route('/map')
def map():
    '''Render the main map page'''
    return render_template('map.html')

@frontend.route('/map/markers/<ne_lat>,<ne_lng>,<sw_lat>,<sw_lng>.json')
def get_markers(ne_lat, ne_lng, sw_lat, sw_lng):
    '''
    Return markers related to the given frame.
    Send them in a json format
    '''
    markers = Report.query \
        .filter(Report.latitude < ne_lat) \
        .filter(Report.latitude > sw_lat) \
        .filter(Report.longitude < ne_lng) \
        .filter(Report.longitude > sw_lng) \
        .all()

    json_markers = { 'markers' : [ marker.__json__() for marker in markers ] }
    return jsonify(json_markers)
