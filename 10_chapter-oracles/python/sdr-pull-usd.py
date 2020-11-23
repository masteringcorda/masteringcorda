import argparse
import pandas as pd

def parse_imf_sdrv(path):
    
    colnames = ['report_date', 'exchange_rate', 'usd_equivalent']

    df = pd.read_csv(path, sep='\t', skiprows=3, names=colnames, usecols=[0, 3, 4])
    df = df.dropna(how='all').dropna(subset=['usd_equivalent'])
    df.report_date = pd.to_datetime(df.report_date)
    
    sdr = df[df['report_date'].isnull()].reset_index(drop=True)
    sdr['report_date'] = pd.Series(df['report_date'].dropna().unique()).repeat(3).reset_index(drop=True)
    
    df_1usd = sdr.dropna(subset=['exchange_rate'])[sdr.dropna(subset=['exchange_rate']).exchange_rate.str.contains('U.S')].drop(columns=['exchange_rate']).rename(columns={'usd_equivalent': 'US$1'})
    df_1usd = df_1usd.reset_index(drop=True)
    
    df_1sdr = sdr.dropna(subset=['exchange_rate'])[sdr.dropna(subset=['exchange_rate']).exchange_rate.str.contains('SDR1')].drop(columns=['exchange_rate']).rename(columns={'usd_equivalent': 'SDR1'})
    df_1sdr = df_1sdr.reset_index(drop=True)

    return df_1usd.merge(df_1sdr, on=['report_date'])


def main(infile, outfile):

    df = parse_imf_sdrv(infile)

    if outfile is None:
        print(df.to_csv(index=False, float_format='%.6f'))
    else:
        df.to_csv(outfile, index=False, float_format='%.6f')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-in', '--infile', help='sdrv.xls input file', required=True)
    parser.add_argument('-out', '--outfile', help='output csv path', default=None)

    args  = parser.parse_args()
    kwargs = vars(args)
    main(**kwargs)