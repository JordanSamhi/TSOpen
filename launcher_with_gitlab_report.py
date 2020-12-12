import subprocess
import sys
import gitlab

if __name__ == "__main__":
    if len(sys.argv) < 5:
        print("Usage: python3 launcher.py APK PLATFORMS GITLAB_TOKEN GITLAB_PROJECT_ID")
        sys.exit(1)
    app = sys.argv[1]
    platforms = sys.argv[2]
    token = sys.argv[3]
    project_id = sys.argv[4]
    cmd = f"java -jar target/TSOpen-1.0-jar-with-dependencies.jar -f {app} -p {platforms} -r"
    process = subprocess.Popen(f"{cmd}", stdout=subprocess.PIPE, shell=True)
    out,err = process.communicate()
    output = out.decode("utf8")
    lbs = output.split('\n')
    issue_message = ""
    lb_found = False
    for lb in lbs:
        if lb and not lb.startswith("%"):
            split_app = lb.split(',')
            sha = split_app[0]
            pkg = split_app[1]
            issue_message += f"TSOpen found potential security issue(s):\n"
            issue_message += f"* Package name: {pkg}\n"
            issue_message += f"* SHA-256: {sha}\n"
        elif lb.startswith("%"):
            lb_found = True
            split_lb = lb.split(';')
            clazz = split_lb[1]
            method = split_lb[2]
            plb = split_lb[12]
            issue_message += f"\nPotential hidden sensitive behavior found:\n"
            issue_message += f"* Class: {clazz}\n"
            issue_message += f"* Method: {method}\n"
            issue_message += f"* Potential trigger: {plb}\n"
    if lb_found:
        issue_message += "\nPlease verify the app for addressing the potential issues.\n\n"
        issue_message += "Visit [TSOpen](https://github.com/JordanSamhi/TSOpen) for more information."
        gl = gitlab.Gitlab("https://gitlab.com", private_token=token)
        gl.auth()
        project = gl.projects.get(project_id)
        issue = project.issues.create({
            "title": "Potential hidden triggered code found",
            "description": issue_message
            })
        print(f"Issue successfully created (id: {issue.get_id()})")
    else:
        print(f"No sensitive behavior found by TSOpen.")
