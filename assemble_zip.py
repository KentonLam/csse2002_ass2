#!/usr/bin/env python3

import os
import sys
import zipfile
import shutil
import subprocess

src_files = {
    'src_path': 'src/main/java/',
    'dest_path': 'src/',
    'include': [ x+'.java' for x in
        ('Block', 'BlockWorldException', 'Builder', 'GrassBlock',
        'GroundBlock', 'InvalidBlockException', 'NoExitException',
        'SoilBlock', 'StoneBlock', 'Tile', 'TooHighException',
        'TooLowException', 'WoodBlock')
    ]
}

test_files = {
    'src_path': 'src/test/java/',
    'dest_path': 'test/',
    'include': [
        'GrassBlockTest.java', 'TileTest.java'
    ]
}


def main():
    print('Starting artifact build...')
    print('Copying to temp directory...')
    shutil.copytree('.', './__temp')
    os.chdir('./__temp')

    print('Deleting spurious source files...')
    for f in os.listdir(src_files['src_path']):
        if f not in src_files['include']:
            print('    Deleted', f)
            os.unlink(src_files['src_path'] + '/' + f)
        else:
            print('    Kept', f)

    print('Executing tests...')
    test_result = subprocess.call(['mvn clean test -B'], shell=True)
    if test_result != 0:
        sys.exit(test_result)

    print('Compiling artifact zip...')

    if len(sys.argv) < 2:
        print('Requires zip name argument.')
        sys.exit(1)

    print('Writing zip file', sys.argv[1])

    zf = zipfile.ZipFile('./../'+sys.argv[1], 'w')

    for file_structure in (src_files, test_files):
        for f in file_structure['include']:
            print('    Adding', file_structure['src_path']+f)
            zf.write(file_structure['src_path']+f, file_structure['dest_path']+f)
    print('Removing temp directory...')
    os.chdir('..')
    shutil.rmtree('__temp')
    print('Done.')





if __name__ == '__main__':
    main()