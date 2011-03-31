import os
from flask import Module, request, current_app
from werkzeug import secure_filename

frontend = Module(__name__)

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in current_app.config['ALLOWED_EXTENSIONS']

@frontend.route('/report/<latitude>,<longitude>', methods=['GET', 'PUT'])
def upload(latitude, longitude):
    if request.method == 'PUT':
        file = request.files['file']
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            print filename
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
