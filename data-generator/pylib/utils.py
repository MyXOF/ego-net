import sys


def get_current_directory():
    if sys.path:
        work_path = sys.path[0]
        return work_path
    return ''

if __name__ == "__main__":
    print(get_current_directory())
    pass