from flask import Module, render_template
from urbanjungle.models.report import Report

backend = Module(__name__, 'admin')

@backend.route('/reports')
def list_reports():
  reports = Report.query.all()
  return render_template('admin/report_list.html', reports=reports)
