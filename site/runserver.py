from urbanjungle import app

if __name__ == '__main__':
    if app.debug:
        app.run(host='0.0.0.0', port=8080, debug=True)
    else:
        app.run()
