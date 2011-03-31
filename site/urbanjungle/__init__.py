import os
from flask import Flask, request, redirect, url_for
from werkzeug import secure_filename

app = Flask(__name__)
if os.getenv('DEV') == 'yes':
    app.config.from_object('urbanjungle.config.DevelopmentConfig')
elif os.getenv('TEST') == 'yes':
    app.config.from_object('urbanjungle.config.TestConfig')
else:
    app.config.from_object('urbanjungle.config.ProductionConfig')

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in app.config['ALLOWED_EXTENSIONS']

@app.route('/upload', methods=['GET', 'POST'])
def upload():
    if request.method == 'POST':
        file = request.files['file']
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            print filename
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            return ''
        else:
            return 'File not allowed', 403
    else:
        return '''
            <!doctype html>
            <title>Upload new File</title>
            <h1>Upload new File</h1>
            <form action="" method=post enctype=multipart/form-data>
            <p><input type=file name=file>
            <input type=submit value=Upload>
            </form>
        '''
