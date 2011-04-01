import sys
sys.path.append('/var/www/urbanjungle/site/')

activate_this = '/usr/local/pythonenv/flask/bin/activate_this.py'
execfile(activate_this, dict(__file__=activate_this))

from urbanjungle import app as application

# Uncomment to paste log exceptions in log file
#from paste.exceptions.errormiddleware import ErrorMiddleware
#application = ErrorMiddleware(application, debug=True,
#    error_log="/tmp/log.log", show_exceptions_in_wsgi_errors=True)
