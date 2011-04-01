import sys
sys.path.append('/var/www/urbanjungle/site/')

activate_this = '/usr/local/pythonenv/flask/bin/activate_this.py'
execfile(activate_this, dict(__file__=activate_this))

from urbanjungle import app as application
